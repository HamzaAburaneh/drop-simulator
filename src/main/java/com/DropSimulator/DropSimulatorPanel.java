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

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;

import javax.swing.*;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class DropSimulatorPanel extends PluginPanel {

    private final DropSimulatorPlugin myPlugin;
    private final DropSimulatorConfig myConfig;

    private ArrayList<Drop> simulatedDrops = new ArrayList<>();

    // Panel displaying search bar
    private JPanel searchPanel = new JPanel();
    public IconTextField searchBar = new IconTextField();
    private JButton btn_searchButton = new JButton("Search");

    // Panel displaying info
    private JPanel infoPanel = new JPanel(new GridBagLayout());
    // Drop source indicators
    private JLabel lbl_dropSource = new JLabel();
    public JLabel txt_SourceName = new JLabel("--");
    private JPopupMenu menu_dropSource = new JPopupMenu("Source");
    // Trial number indicators
    private JLabel lbl_numTrials = new JLabel();
    public JSpinner spinner_numTrials = new JSpinner();
    private JPopupMenu menu_numTrials = new JPopupMenu("Trials");
    // Value indicators
    private JLabel lbl_totalValue = new JLabel();
    public JLabel txt_totalValue = new JLabel("--");
    private JPopupMenu menu_totalValue = new JPopupMenu("Value");

    // Panel displaying simulated drop trials
    public JPanel trialsPanel = new JPanel();

    // Source icon
    private BufferedImage sourceImage = ImageUtil.loadImageResource(getClass(), "/source_icon.png");
    private Icon sourceIcon = new ImageIcon(sourceImage);

    // Trials icon
    private BufferedImage trialsImage = ImageUtil.loadImageResource(getClass(), "/trials_icon.png");
    private Icon trialsIcon = new ImageIcon(trialsImage);

    // Value icon
    private BufferedImage valueImage = ImageUtil.loadImageResource(getClass(), "/value_icon.png");
    private Icon valueIcon = new ImageIcon(valueImage);

    private DatabaseParser myParser;
    private ItemManager myManager;

    private long totalValue;


    @Inject
    DropSimulatorPanel(final DropSimulatorPlugin plugin, final DropSimulatorConfig config, final ItemManager manager){

        myParser = new DatabaseParser(config);

        this.myPlugin = plugin;
        this.myConfig = config;
        this.myManager = manager;

        /*
         * Overall DropSimulatorPanel
         */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Border myBorder = BorderFactory.createEmptyBorder(7,7,7,7);
        setBorder(myBorder);

        /*
         * searchPanel
         */
        add(searchPanel, BorderLayout.NORTH);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchBar, BorderLayout.NORTH);

        // search bar
        searchBar.setFocusable(false);
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setPreferredSize(new Dimension(0,30));
        add(Box.createVerticalStrut(5));
        searchBar.addActionListener(e -> {

            try {
                onSearch();
            } catch (IOException | ParseException e1) {
                e1.printStackTrace();
            }

        });

        // search button
        searchPanel.add(btn_searchButton, BorderLayout.CENTER);
        btn_searchButton.setFocusable(false);
        btn_searchButton.setBackground(ColorScheme.SCROLL_TRACK_COLOR);
        add(Box.createVerticalStrut(5));
        btn_searchButton.addActionListener(e -> {

            try {
                onSearch();
            } catch (IOException | ParseException e1) {
                e1.printStackTrace();
            }

        });

        /*
         * infoPanel - shows all info about the trial
         */

        add(infoPanel, BorderLayout.CENTER);
        BorderLayout customLayout = new BorderLayout();
        customLayout.setVgap(10);
        infoPanel.setLayout(customLayout);

        JPanel infoPanelNorth = new JPanel();
        JPanel infoPanelCenter = new JPanel();
        JPanel infoPanelSouth = new JPanel();

        infoPanelNorth.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        infoPanelCenter.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        infoPanelSouth.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        infoPanel.add(infoPanelNorth, BorderLayout.NORTH);
        infoPanel.add(infoPanelCenter, BorderLayout.CENTER);
        infoPanel.add(infoPanelSouth, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();

        /*
         * infoPanelNorth - northern panel of the info panel
         */

        // drop source label
        infoPanelNorth.add(lbl_dropSource);
        lbl_dropSource.setIcon(sourceIcon);
        lbl_dropSource.setComponentPopupMenu(menu_dropSource);

        // drop source text box
        infoPanelNorth.add(txt_SourceName);
        txt_SourceName.setHorizontalAlignment(SwingConstants.LEFT);

        /*
         * infoPanelCenter - center panel of the info panel
         */

        // number of trials label
        infoPanelCenter.setBorder(new EmptyBorder(-2,0,-2,0));
        infoPanelCenter.add(lbl_numTrials);
        lbl_numTrials.setIcon(trialsIcon);
        lbl_numTrials.setComponentPopupMenu(menu_numTrials);

        // number of trials j spinner
        infoPanelCenter.add(spinner_numTrials);
        spinner_numTrials.setValue(config.simulatedTrialsConfig());
        spinner_numTrials.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        spinner_numTrials.setBorder(new EmptyBorder(0,0,0,0));

        /*
         * infoPanelSouth - southern panel of the info panel
         */

        // total value label
        infoPanelSouth.add(lbl_totalValue);
        lbl_totalValue.setIcon(valueIcon);

        // total value text box
        infoPanelSouth.add(txt_totalValue);
        txt_totalValue.setFocusable(false);
        txt_totalValue.setHorizontalAlignment(SwingConstants.LEFT);
        add(Box.createVerticalStrut(10));

        // trials Panel
        add(trialsPanel, BorderLayout.SOUTH);
        trialsPanel.setBorder(new EmptyBorder(0,0,0,-1));

    }

    @Override
    public void onActivate() {
        super.onActivate();
        searchBar.requestFocusInWindow();
    }

    public void onSearch() throws IOException, ParseException {

        searchBar.requestFocusInWindow();
        trialsPanel.setVisible(false);
        trialsPanel.getComponentPopupMenu();
        txt_SourceName.setText("--");
        txt_totalValue.setText("--");

        Window[] windows = Window.getWindows();
        for (Window window : windows){
            if(window.getType().toString().equals("POPUP")){
                window.dispose();
            }
        }

        Thread t1 = new Thread(() -> {

            searchBar.setIcon(IconTextField.Icon.LOADING);

            try {
                spinner_numTrials.commitEdit(); // properly updates jspinner when search pressed
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            String searchText = searchBar.getText();
            DropTable myTable = null; // id of 0 means it is a search
            try {
                myTable = myParser.acquireDropTable(searchText,0);
            } catch (IOException e) {
                searchBar.setIcon(IconTextField.Icon.ERROR);
            } catch (NumberFormatException e){
                searchBar.setIcon(IconTextField.Icon.ERROR);
            }

            ArrayList<Drop> myDrops = myTable.runTrials((int) spinner_numTrials.getValue());
            buildDropPanels(myDrops, myTable.getName());

            searchBar.setIcon(IconTextField.Icon.SEARCH);

        });

        t1.start();

    }

    /*
     * buildDropPanels builds the panels that display each drop
     */

    public void buildDropPanels(ArrayList<Drop> myDrops, String dropSource){
        SwingUtilities.invokeLater(() -> {

            trialsPanel.setVisible(false);

            if(trialsPanel.getComponentPopupMenu()!= null) {
                trialsPanel.getComponentPopupMenu().setVisible(false);
            }

            trialsPanel.removeAll();
            totalValue = 0;
            simulatedDrops = myDrops;
            txt_SourceName.setText(dropSource);

            trialsPanel.setLayout(new GridLayout(0,5));

            for (Drop d : simulatedDrops) {

                int quantity = Integer.parseInt(d.getQuantity());
                AsyncBufferedImage myImage = myManager.getImage(d.getId(),quantity,true);
                long value = (long) myManager.getItemPrice(d.getId()) *quantity;
                DropPanel myDropPanel = new DropPanel(myImage,d,value);
                totalValue += value;
                trialsPanel.add(myDropPanel);

            }

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedValue = formatter.format(totalValue);
            txt_totalValue.setText(formattedValue);
            trialsPanel.setVisible(true);


        });
    }

}
