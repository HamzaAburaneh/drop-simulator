/*
 * Copyright (c) 2021, Marshall <https://github.com/mxp190009>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.DropSimulator;

import com.google.gson.JsonArray;
import com.google.inject.Provides;

import javax.imageio.ImageIO;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
	name = "Drop Simulator",
	description ="Simulates Trials of NPC Drop Tables"

)
public class DropSimulatorPlugin extends Plugin
{
	private String SIMULATE = "Simulate Drops";
	private NavigationButton navButton;
	private DropSimulatorPanel myPanel;
	private ClientThread myClientThread;

	public DropSimulatorPlugin(){

		myClientThread = new ClientThread();

	}

	BufferedImage myIcon;

	{
		try {
			myIcon = ImageIO.read(this.getClass().getResourceAsStream("/Drop Simulator Icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * onMenuOpened adds the 'Simulate Drops' menu option when an NPC is right clicked
	 */

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened){

		if(config.rightClickMenuConfig()) {

			NPC[] myNPCs = client.getCachedNPCs();
			MenuEntry[] myEntries = menuOpened.getMenuEntries();

			for (MenuEntry menuEntry : myEntries) {

				if (menuEntry.getOption().equals("Attack")) {    // if there is an attack menu entry

					NPC myNPC = myNPCs[menuEntry.getIdentifier()];

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

			int targetID = menuOptionClicked.getId();
			NPC myNPC = myNPCs[targetID];

			ApiParser myParser = new ApiParser();
			JsonArray myArray = myParser.acquireDropTable(myNPC.getId());

			DropTable myTable = new DropTable(myArray,myNPC.getName(),config);
			ArrayList<Drop> myDrops = myTable.runTrials((int)myPanel.spnr_numTrials.getValue());
			ArrayList<Drop> toBeRemoved = new ArrayList<Drop>();

			// Using coins as an example - if coins take up any number of drops on a drop table > 1, for example 3;
			// the arrayList of drops will return the total dropped number of coins as 3 separate drops. For example,
			// Nechryael have 6 different coin drops. If the total number of dropped coins was 500k, the arraylist
			// of drops will return 6 different drops of coins all of which are 500k. The following code
			// removes the duplicates from the list leaving only the single correct 500k coin total.
			for(Drop d : myDrops){
				int duplicate = 0;

				for(Drop k : myDrops){

					if(d.sameID(k)){
						duplicate++;

						if(duplicate > 1){ // if it paired with more than just itself
							toBeRemoved.add(k);

						}
					}
				}
			}

			for(Drop d : toBeRemoved) {
				myDrops.remove(d);

			}

			myPanel.buildDropPanels(myDrops,myNPC.getName());

		}

	}

	@Inject
	private Client client;

	@Inject
	private DropSimulatorConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager manager;

	@Override
	protected void startUp() throws Exception
	{
		myPanel = new DropSimulatorPanel(this, config, manager);

		navButton = NavigationButton.builder()
				.tooltip("Drop Simulator")
				.icon(myIcon)
				.priority(50)
				.panel(myPanel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Provides
	DropSimulatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DropSimulatorConfig.class);
	}
}
