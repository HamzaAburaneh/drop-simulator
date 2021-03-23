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
import net.runelite.client.ui.PluginPanel;

import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;

import javax.swing.*;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class DropSimulatorPanel extends PluginPanel {

    private final DropSimulatorPlugin myPlugin;
    private final DropSimulatorConfig myConfig;

    //
    private ArrayList<Drop> simulatedDrops = new ArrayList<Drop>();

    // Panel displaying search bar
    private JPanel searchPanel = new JPanel();
    private JTextField searchBar = new JTextField();
    private JButton btn_searchButton = new JButton("Search");

    // Panel displaying info
    private JPanel infoPanel = new JPanel(new GridBagLayout());
    private JLabel lbl_dropSource = new JLabel("Source: ", JLabel.TRAILING);
    private JTextField txt_SourceName = new JTextField(" ");
    private JLabel lbl_numTrials = new JLabel("Trials: ", JLabel.TRAILING);
    public JSpinner spnr_numTrials = new JSpinner();
    private JLabel lbl_totalValue = new JLabel("Value: ", JLabel.TRAILING);
    private JTextField txt_totalValue = new JTextField(" ");

    // Panel displaying simulated drop trials
    public JPanel trialsPanel = new JPanel();

    private DatabaseParser myParser;
    private ItemManager myManager;

    private long totalValue;

    @Inject
    DropSimulatorPanel(final DropSimulatorPlugin plugin, final DropSimulatorConfig config, final ItemManager manager){

        myParser = new DatabaseParser(config);

        this.myPlugin = plugin;
        this.myConfig = config;
        this.myManager = manager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Border myBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(myBorder);

        // Search Panel
        add(searchPanel, BorderLayout.NORTH);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchBar, BorderLayout.NORTH);
        searchPanel.add(btn_searchButton, BorderLayout.CENTER);
        btn_searchButton.setFocusable(false);
        add(Box.createVerticalStrut(5));
        btn_searchButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    onSearchPressed(e);
                } catch (IOException | ParseException | SQLException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        // Info Panel
        add(infoPanel, BorderLayout.CENTER);
        GridBagConstraints c = new GridBagConstraints();
        infoPanel.setBorder(new LineBorder(Color.BLACK));

        c.ipady = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(3,3,0,0);
        infoPanel.add(lbl_dropSource,c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3,0,0,3);
        infoPanel.add(txt_SourceName,c);

        txt_SourceName.setEditable(false);
        txt_SourceName.setFocusable(false);
        txt_SourceName.setHorizontalAlignment(SwingConstants.RIGHT);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(3,0,0,0);
        infoPanel.add(lbl_numTrials,c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3,0,0,3);
        infoPanel.add(spnr_numTrials,c);

        spnr_numTrials.setValue(config.simulatedTrialsConfig());
        spnr_numTrials.setVerifyInputWhenFocusTarget(true);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(3,0,3,0);
        infoPanel.add(lbl_totalValue,c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3,0,3,3);
        infoPanel.add(txt_totalValue,c);
        txt_totalValue.setEditable(false);
        txt_totalValue.setFocusable(false);
        txt_totalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        add(Box.createVerticalStrut(5));

        // Trials Panel
        add(trialsPanel, BorderLayout.SOUTH);

    }

    public void onSearchPressed(ActionEvent e) throws IOException, ParseException, SQLException {

        trialsPanel.setVisible(false);
        spnr_numTrials.commitEdit(); // properly updates jspinner when search pressed
        String searchText = searchBar.getText();
        DropTable myTable = myParser.acquireDropTable(searchText);
        ArrayList<Drop> myDrops = myTable.runTrials((int) spnr_numTrials.getValue());
        buildDropPanels(myDrops, myTable.getName());

    }

    /*
     * buildDropPanels builds the panels that display each drop
     */

    public void buildDropPanels(ArrayList<Drop> myDrops, String dropSource){
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {

                trialsPanel.setVisible(false);
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


            }
        });
    }
}
