package com.DropSimulator;

import com.google.gson.JsonArray;

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

import java.text.DecimalFormat;
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
    private JLabel lbl_monsterName = new JLabel("Monster: ", JLabel.TRAILING);
    private JTextField txt_monsterName = new JTextField(" ");
    private JLabel lbl_numTrials = new JLabel("Trials: ", JLabel.TRAILING);
    public JSpinner spnr_numTrials = new JSpinner();
    private JLabel lbl_totalValue = new JLabel("Value: ", JLabel.TRAILING);
    private JTextField txt_totalValue = new JTextField(" ");

    // Panel displaying simulated drop trials
    private JPanel trialsPanel = new JPanel();

    private ApiParser myParser;
    private ItemManager myManager;

    private long totalValue;

    @Inject
    DropSimulatorPanel(final DropSimulatorPlugin plugin, final DropSimulatorConfig config, final ItemManager manager){

        myParser = new ApiParser();

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
                } catch (IOException ioException) {
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
        infoPanel.add(lbl_monsterName,c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3,0,0,3);
        infoPanel.add(txt_monsterName,c);

        txt_monsterName.setEditable(false);
        txt_monsterName.setFocusable(false);
        txt_monsterName.setHorizontalAlignment(SwingConstants.RIGHT);

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

    public void onSearchPressed(ActionEvent e) throws IOException {

        trialsPanel.setVisible(false);
        String searchText = searchBar.getText();
        ArrayList<Object> myObjects = myParser.acquireDropTable(searchText);
        JsonArray myArray = (JsonArray)myObjects.get(0);
        String finalName = (String)myObjects.get(1);
        DropTable myTable = new DropTable(myArray,searchText,myConfig);
        ArrayList<Drop> myDrops = myTable.runTrials((int)spnr_numTrials.getValue());
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

        buildDropPanels(myDrops,finalName);


    }

    public void buildDropPanels(ArrayList<Drop> myDrops, String monsterName){
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                trialsPanel.removeAll();
                trialsPanel.setVisible(false);
                totalValue = 0;
                simulatedDrops = myDrops;
                txt_monsterName.setText(monsterName);

                double totalDrops = 0.0;

                for(Drop d : simulatedDrops){

                    if(Integer.parseInt(d.getQuantity()) > 0){

                        totalDrops++;

                    }

                }

                double cols = 5.0;
                double rows = Math.ceil(totalDrops/cols);

                trialsPanel.setLayout(new GridLayout((int)rows,(int)cols));

                for (Drop d : simulatedDrops) {
                    int quantity = Integer.parseInt(d.getQuantity());
                    if (quantity > 0) {

                        AsyncBufferedImage myImage = myManager.getImage(d.getId(),quantity,true);
                        long value = myManager.getItemPrice(d.getId())*quantity;
                        DropPanel myDropPanel = new DropPanel(myImage,d,value);
                        totalValue += value;
                        trialsPanel.add(myDropPanel);

                    }
                }

                DecimalFormat formatter = new DecimalFormat("#,###,###");
                String formattedValue = formatter.format(totalValue);
                txt_totalValue.setText(formattedValue);
                trialsPanel.setVisible(true);

                getParent().validate();
                getParent().repaint();

            }
        });
    }
}
