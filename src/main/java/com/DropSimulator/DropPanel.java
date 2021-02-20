package com.DropSimulator;

import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

public class DropPanel extends JPanel{

    private JPopupMenu popupDescription;
    private JLabel lbl_myImage;

    public DropPanel(AsyncBufferedImage icon, Drop myDrop, long value){

        // panel settings - size/border/layout
        setPreferredSize(new Dimension(34,44));
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setLayout(new GridBagLayout());

        // popup description when icon is hovered over
        popupDescription = new JPopupMenu();
        popupDescription.setVisible(false);

        // add commas to the values
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedValue = formatter.format(value);
        String formattedQuantity = formatter.format(Long.parseLong(myDrop.getQuantity()));

        String dropDescription = myDrop.getName() + " x " + formattedQuantity;
        String displayValue = "Value: " + formattedValue;

        popupDescription.add(dropDescription);
        popupDescription.add(displayValue);

        // icon displayed
        ImageIcon myIcon = new ImageIcon(icon);
        lbl_myImage = new JLabel();
        lbl_myImage.setIcon(myIcon);
        icon.addTo(lbl_myImage);
        add(lbl_myImage);

        // mouse listener to display popup description
        addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent e) {

                onMouseEntered(e);

            }

            @Override
            public void mouseExited(MouseEvent e) {

                onMouseExited(e);

            }

        });
    }

    public void onMouseEntered(MouseEvent e){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){

                Point mouseLocation = e.getLocationOnScreen();
                popupDescription.setLocation(mouseLocation);
                popupDescription.setVisible(true);

            }
        });

    }

    public void onMouseExited(MouseEvent e){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){

                popupDescription.setVisible(false);

            }
        });
    }
}
