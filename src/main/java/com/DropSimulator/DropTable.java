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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DropTable {

    private ArrayList<Drop> preRollDrops;
    private ArrayList<Drop> alwaysDrops;
    private ArrayList<Drop> mainDrops;
    private ArrayList<Drop> tertiaryDrops;
    private ArrayList<Drop> catacombTertiaryDrops;
    private ArrayList<Drop> wildernessSlayerTertiaryDrops;
    private boolean prevDropPreRoll = false; // keeps track of if the previous drop was a pre roll
    private boolean multipleRolls = false; // determines if a drop table is rolling multiple rolls
    private DropSimulatorConfig config;

    public DropTable(JsonArray jsonDrops, String npcName, DropSimulatorConfig config) throws IOException {

        this.preRollDrops = new ArrayList<Drop>();
        this.alwaysDrops = new ArrayList<Drop>();
        this.mainDrops = new ArrayList<Drop>();
        this.tertiaryDrops = new ArrayList<Drop>();
        this.catacombTertiaryDrops = new ArrayList<Drop>();
        this.wildernessSlayerTertiaryDrops = new ArrayList<Drop>();
        this.config = config;

        String wikiPage = "https://oldschool.runescape.wiki/w/" + npcName;
        Document doc = Jsoup.connect(wikiPage).get(); // connects to wikipedia page
        Elements tertiaryTable = doc.select("span#Tertiary"); // gets tertiary table
        Elements catacombsTable = doc.select("span#Catacombs_tertiary"); // gets catacombs table
        Elements preRollTable = doc.select("span#Pre-roll"); // gets pre-roll table
        Elements uniqueTable = doc.select("span#Uniques"); // gets unique

        // gets wilderness slayer tertiary table
        Elements wildernessSlayerTable = doc.select("span#Wilderness_Slayer_tertiary");

        // for each drop in the api
        for(int i = 0; i < jsonDrops.size(); i++){

            JsonObject myObj = (JsonObject) jsonDrops.get(i);
            JsonElement jsonId = myObj.get("id");
            JsonElement jsonQuantity = myObj.get("quantity");
            JsonElement jsonRolls = myObj.get("rolls");
            JsonElement jsonRarity = myObj.get("rarity");
            JsonElement jsonName = myObj.get("name");

            // create a new drop object
            Drop myDrop = new Drop(
                    jsonId.getAsInt(),
                    jsonQuantity.getAsString(),
                    jsonRolls.getAsInt(),
                    jsonRarity.getAsDouble(),
                    jsonName.getAsString());

            // determine if the drop is tertiary
            try {
                myDrop.determineTertiary(tertiaryTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // determine if the drop is a catacombs only drop
            try {
                myDrop.determineCatacomb(catacombsTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // determine if the drop is a pre-roll drop
            try {
                myDrop.determinePreRoll(preRollTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // determine if the drop is a unique
            try {
                myDrop.determineUnique(uniqueTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // determine if the drop is a wilderness slayer drop
            try {
                myDrop.determineWildernessSlayer(wildernessSlayerTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(myDrop.isTertiary()){ // if the drop is a tertiary drop

                this.tertiaryDrops.add(myDrop);

            } else if(myDrop.isPreRoll() || myDrop.isUnique()){

                this.preRollDrops.add(myDrop);

            } else if (myDrop.isCatacomb()) {

                this.catacombTertiaryDrops.add(myDrop);

            } else if (myDrop.isWildernessSlayer()){

                this.wildernessSlayerTertiaryDrops.add(myDrop);

            } else if (myDrop.getRarity() == 1.0){

                this.alwaysDrops.add(myDrop);

            } else {

                this.mainDrops.add(myDrop);

            }

        }
    }

    public ArrayList<Drop> runTrials(int n){

        ArrayList<Drop> finalSimulatedDrops = new ArrayList<Drop>();
        Random randy = new Random();

        // creates a table of all drops for each table with 0 quantity
        ArrayList<Drop> emptyAlways = emptyTable(alwaysDrops);
        ArrayList<Drop> emptyPreRoll = emptyTable(preRollDrops);
        ArrayList<Drop> emptyMain = emptyTable(mainDrops);
        ArrayList<Drop> emptyTertiary= emptyTable(tertiaryDrops);
        ArrayList<Drop> emptyCatacombs = emptyTable(catacombTertiaryDrops);
        ArrayList<Drop> emptyWilderness = emptyTable(wildernessSlayerTertiaryDrops);

        // creates a drop interval for each table
        ArrayList<Double> dropIntervalsPreRoll = partitionDrops(preRollDrops);
        ArrayList<Double> dropIntervalsMain = partitionDrops(mainDrops);
        ArrayList<Double> dropIntervalsTertiary = partitionDrops(tertiaryDrops);
        ArrayList<Double> dropIntervalsCatacombs = partitionDrops(catacombTertiaryDrops);
        ArrayList<Double> dropIntervalsWilderness = partitionDrops(wildernessSlayerTertiaryDrops);

        // determine how many rolls the monster has
        int numRolls = emptyMain.get(0).getRolls();

        if(numRolls > 0){
            multipleRolls = true;
        }

        rollAlwaysTable(emptyAlways,n); // always table only has one roll, hence it is rolled here

        for(Drop d: emptyAlways){
            finalSimulatedDrops.add(d);
        }


        if(multipleRolls){
            n *= numRolls;
        }

        for(int i = 0; i < n; i++) {

            rollPreRollTable(emptyPreRoll, dropIntervalsPreRoll, preRollDrops);

            if (!prevDropPreRoll) {
                rollTable(emptyMain, dropIntervalsMain, mainDrops);
            }

            prevDropPreRoll = false;
        }

        if(multipleRolls){
            n = n/numRolls
;        }

        // tertiary tables are only rolled once, so they are rolled with the trial number rather than number of rolls
        for(int i = 0; i < n; i ++){

            rollTable(emptyTertiary, dropIntervalsTertiary, tertiaryDrops);

            if(config.catacombConfig()) {
                rollTable(emptyCatacombs, dropIntervalsCatacombs, catacombTertiaryDrops);
            }

            if(config.wildernessConfig()) {
                rollTable(emptyTertiary, dropIntervalsWilderness, wildernessSlayerTertiaryDrops);
            }

        }

        for(Drop d: emptyPreRoll) {
            finalSimulatedDrops.add(d);
        }

        // because some always drops are also main drops, it must be checked if the main drop has already been added
        // through the always drops

        ArrayList<Drop> toBeRemoved = new ArrayList<Drop>();

        for(Drop d: emptyMain) {
            for(Drop k : finalSimulatedDrops){

                if(d.sameID(k)){ // if the drop has already been added

                    int prevQuant = Integer.parseInt(d.getQuantity());
                    int quantToBeAdded = Integer.parseInt(k.getQuantity());
                    int quantity = prevQuant + quantToBeAdded;
                    k.setQuantity(Integer.toString(quantity));
                    toBeRemoved.add(d);
                }

            }

        }

        System.out.println(toBeRemoved);

        for(Drop d: toBeRemoved){
            emptyMain.remove(d);
        }

        for(Drop d: emptyMain){
            finalSimulatedDrops.add(d);
        }

        for(Drop d: emptyTertiary) {
            finalSimulatedDrops.add(d);
        }

        for(Drop d: emptyCatacombs) {
            finalSimulatedDrops.add(d);
        }

        for(Drop d: emptyWilderness) {
            finalSimulatedDrops.add(d);
        }

        return finalSimulatedDrops;
    }

    /*
     * the rollAlwaysTable rolls the monsters guaranteed drop table.
     */

    public void rollAlwaysTable(ArrayList<Drop> emptyTable, Integer n) {

        boolean guaranteedInterval = false;

        for (int i = 0; i < this.alwaysDrops.size(); i++) { // for each 100% drop

            if (alwaysDrops.get(i).getQuantity().contains("-")) { // if one of the 100% drops is an interval drop

                guaranteedInterval = true;

            }
        }

        if(guaranteedInterval){ // if there exists a guaranteed drop with an interval drop range

            Random randy = new Random();

            for (int j = 0; j < n; j++) {
                for (int i = 0; i < this.alwaysDrops.size(); i++) {

                    String interval = alwaysDrops.get(i).getQuantity();

                    if (interval.contains("-")) { // if this is the drop with the interval

                        String[] boundaries = interval.split("-");
                        int lowerRange = Integer.parseInt(boundaries[0]);
                        int higherRange = Integer.parseInt(boundaries[1]);
                        int quantity = randy.nextInt(higherRange - lowerRange) + lowerRange;
                        int prevQuant = Integer.parseInt(emptyTable.get(i).getQuantity()); // previous quantity
                        int quantToBeAdded = prevQuant + quantity;
                        emptyTable.get(i).setQuantity(Integer.toString(quantToBeAdded));

                    } else { // if it is not the drop with the interval

                        int amountPerDrop = Integer.parseInt(alwaysDrops.get(i).getQuantity());
                        int total = amountPerDrop * n;
                        emptyTable.get(i).setQuantity(Integer.toString(total));

                    }

                }
            }

        } else { // if all drops have a specified quantity

            for (int i = 0; i < this.alwaysDrops.size(); i++) {

                int amountPerDrop = Integer.parseInt(alwaysDrops.get(i).getQuantity());
                int total = amountPerDrop*n;
                emptyTable.get(i).setQuantity(Integer.toString(total));

            }
        }
    }

    /*
     * partitionDrops partitions the drops and returns an array list of doubles which will
     * serve as the intervals for drops.
     */

    public ArrayList<Double> partitionDrops(ArrayList<Drop> tableDrops){

        double totalRarity = 0.0;
        ArrayList<Double> dropIntervals = new ArrayList<Double>();

        dropIntervals.add(totalRarity);

        for(int i = 0; i < tableDrops.size(); i++){ // for each main drop
            totalRarity += tableDrops.get(i).getRarity();
            dropIntervals.add(totalRarity);

        }

        dropIntervals.add(1.0);

        return dropIntervals;

    }

    /*
     * the emptyTable initializes a table with all possible drops but sets the quantity to 0.
     * To finalize the dropped items the empty table will be populated as drops are simulated.
     */

    public ArrayList<Drop> emptyTable(ArrayList<Drop> myList){

        ArrayList<Drop> clonedList = new ArrayList<Drop>();

        for(Drop d: myList){

            clonedList.add(new Drop(d));

        }

        return clonedList;

    }


    /*
     * the rollPreRollTable drops rolls the monsters pre-roll drop table.
     * If a pre-roll is dropped, the main roll is skipped.
     */

    public void rollPreRollTable(ArrayList<Drop> emptyTable, ArrayList<Double> dropIntervals, ArrayList<Drop> tableDrops){
        Random randy = new Random();

        boolean nothing = false; // sets to true if the nothing drop is rolled

            double x = randy.nextDouble(); // rolls a random double between 0.0 and 1.0

            if(x >= dropIntervals.get(dropIntervals.size()-2)){ // if x is greater than the maximum probability
                nothing = true;
            }

            for (int j = 0; j < dropIntervals.size()-2; j++) { // for each drop in the drop interval

                if(nothing){
                    nothing = false;
                    break;
                }

                if (x >= dropIntervals.get(j) && x <= dropIntervals.get(j+1)) { // finds the interval

                    prevDropPreRoll = true;
                    Drop myDrop = tableDrops.get(j); // returns the drop at that interval

                    int quantity;

                    if(myDrop.getQuantity().contains("-")){ // if interval quantity

                        String[] ranges = myDrop.getQuantity().split("-");
                        int lowerRange = Integer.parseInt(ranges[0]);
                        int higherRange = Integer.parseInt(ranges[1]);

                        quantity = randy.nextInt(higherRange - lowerRange) + lowerRange;

                    } else { // otherwise quantity remains normal

                        quantity = Integer.parseInt(myDrop.getQuantity());

                    }

                    for(int k = 0; k < emptyTable.size(); k++){ // runs through the empty table

                        if(myDrop.getId() == emptyTable.get(k).getId()){ // if found

                            int currentQuantity = Integer.parseInt(emptyTable.get(k).getQuantity());
                            int postQuantity = currentQuantity + quantity;

                            emptyTable.get(k).setQuantity(Integer.toString(postQuantity));

                        }

                    }

                    break;
               }
            }

    }

    public void rollTable(ArrayList<Drop> emptyTable, ArrayList<Double> dropIntervals, ArrayList<Drop> tableDrops) {

        if (!emptyTable.isEmpty()) {

            Random randy = new Random();

            boolean nothing = false; // sets to true if the nothing drop is rolled

            double x = randy.nextDouble(); // rolls a random double between 0.0 and 1.0

            if (x >= Collections.max(dropIntervals)) { // if x is greater than the maximum probability
                nothing = true;
            }

            for (int j = 0; j < dropIntervals.size() - 2; j++) { // for each drop in the drop interval

                if (nothing) {
                    nothing = false;
                    break;
                }

                if (x >= dropIntervals.get(j) && x <= dropIntervals.get(j + 1)) { // finds the interval

                    Drop myDrop = tableDrops.get(j); // returns the drop at that interval

                    int quantity;

                    if (myDrop.getQuantity().contains("-")) { // if interval quantity

                        String[] ranges = myDrop.getQuantity().split("-");
                        int lowerRange = Integer.parseInt(ranges[0]);
                        int higherRange = Integer.parseInt(ranges[1]);

                        quantity = randy.nextInt(higherRange - lowerRange) + lowerRange;

                    } else { // otherwise quantity remains normal

                        quantity = Integer.parseInt(myDrop.getQuantity());

                    }

                    for (int k = 0; k < emptyTable.size(); k++) { // runs through the empty table

                        if (myDrop.getId() == emptyTable.get(k).getId()) { // if found

                            int currentQuantity = Integer.parseInt(emptyTable.get(k).getQuantity());
                            int postQuantity = currentQuantity + quantity;

                            emptyTable.get(k).setQuantity(Integer.toString(postQuantity));

                        }

                    }

                    break;
                }
            }

        }

    }
}
