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

//make some changes here for the  security part
package com.DropSimulator;

import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

public class DropPanel extends JPanel{

    private JPopupMenu popupDescription;
    private JLabel lbl_myImage;

    public DropPanel(AsyncBufferedImage icon, Drop myDrop, long value){

        // panel settings - size/border/layout
        setPreferredSize(new Dimension(36,44));
        setBorder(new EtchedBorder(EtchedBorder.RAISED));
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
        SwingUtilities.invokeLater(() -> {

            Point mouseLocation = e.getLocationOnScreen();
            popupDescription.setLocation(mouseLocation);
            popupDescription.setVisible(true);

        });

    }

    public void onMouseExited(MouseEvent e){
        SwingUtilities.invokeLater(() -> popupDescription.setVisible(false));
    }
}
