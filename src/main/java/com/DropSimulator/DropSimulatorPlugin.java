/*
 * Copyright (c) 2021, Marshall <https://github.com/mxp190009>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.DropSimulator;

import com.google.gson.JsonArray;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
	name = "Drop Simulator",
	description ="Simulates Trials of NPC Drop Tables, Clue Tables, Raids, and more"

)
public class DropSimulatorPlugin extends Plugin {
	private String SIMULATE = "Simulate Drops";
	private NavigationButton navButton;
	private DropSimulatorPanel myPanel;
	private BufferedImage myIcon = ImageUtil.loadImageResource(getClass(), "/Drop Simulator Icon.png");

	@Inject
	private Client client;

	@Inject
	private DropSimulatorConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager manager;

	/*
	 * onMenuOpened adds the 'Simulate Drops' menu option when an NPC is right clicked
	 */

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened){

		if(config.rightClickMenuConfig()) {

			MenuEntry[] myEntries = menuOpened.getMenuEntries();

			boolean isPlayer = false;

			for (MenuEntry menuEntry : myEntries) {

				// if the entity is reportable then it is a player
				if (menuEntry.getOption().equals("Report")) {

					isPlayer = true;

				}
			}

			for (MenuEntry menuEntry : myEntries) {

				if (menuEntry.getOption().equals("Attack") && isPlayer == false) { // if attackable and not a player

					int widgetId = menuEntry.getParam1();
					MenuEntry myDropSimulatorMenuEntry = new MenuEntry();
					myDropSimulatorMenuEntry.setOption("Simulate Drops");
					myDropSimulatorMenuEntry.setTarget(menuEntry.getTarget());
					myDropSimulatorMenuEntry.setIdentifier(menuEntry.getIdentifier());
					myDropSimulatorMenuEntry.setParam1(widgetId);
					myDropSimulatorMenuEntry.setType(MenuAction.RUNELITE.getId());
					client.setMenuEntries(ArrayUtils.addAll(myEntries, myDropSimulatorMenuEntry));

				}

			}

		}

	}

	/*
	 * onSimulateDropsClicked simulates a loot trial from the selected NPC
	 */

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) throws IOException {

		NPC[] myNPCs = client.getCachedNPCs();

		if(menuOptionClicked.getMenuOption().equals("Simulate Drops")){
			myPanel.searchBar.setIcon(IconTextField.Icon.LOADING);
			myPanel.trialsPanel.setVisible(false); // setting visible prevents lingering popup menus

			int targetID = menuOptionClicked.getId();
			NPC myNPC = myNPCs[targetID];

			DatabaseParser myParser = new DatabaseParser(config);

			/*
			 * Creating a new thread prevents the game client from lagging
			 */

			Thread t1 = new Thread(() -> {
				Object returned = null;
				try {
					returned = myParser.acquireDropTable(myNPC.getName(),myNPC.getId());
				} catch (IOException e) {
					e.printStackTrace();
				}

				ArrayList<Drop> myDrops;

				if(returned instanceof DropTable){ // if a drop table is returned

					myDrops = ((DropTable) returned).runTrials((int)myPanel.spnr_numTrials.getValue());

				} else { // otherwise if a json array is returned

					DropTable myTable = null;
					try {
						myTable = new DropTable((JsonArray) returned,myNPC.getName(),config);
					} catch (IOException e) {
						myPanel.searchBar.setIcon(IconTextField.Icon.ERROR);
					}
					myDrops = myTable.runTrials((int)myPanel.spnr_numTrials.getValue());

				}

				myPanel.buildDropPanels(myDrops,myNPC.getName());
				myPanel.trialsPanel.setVisible(true);
				myPanel.searchBar.setIcon(IconTextField.Icon.SEARCH);
			});

			t1.start();


		}

	}

	@Override
	protected void startUp() throws Exception
	{
		myPanel = new DropSimulatorPanel(this, config, manager);

		navButton = NavigationButton.builder()
				.tooltip("Drop Simulator")
				.icon(myIcon)
				.priority(9)
				.panel(myPanel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Provides
	DropSimulatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DropSimulatorConfig.class);
	}
}
