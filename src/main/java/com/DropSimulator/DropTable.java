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
    private boolean isNonNpcTable = false; // keeps track if this drop table is of a nonNpc (E.g., Theatre)
    private DropSimulatorConfig config;
    private int preRolled = 0; // total number of prerolled drops
    private String npcName;

    // some tables roll unlike any other table, such as the theatre. If a unique is rolled, the main table is completely
    // skipped, despite the main table having 3 rolls. Therefore specific booleans keep track of these tables.

    // non npc table booleans
    private boolean isTheatre = false;
    private boolean isChambers = false;
    private boolean isGrotGuardians = false;
    private boolean isBarrows = false;
    private boolean isUnsired = false;

    // clue booleans
    private boolean isBeginnerClue = false;
    private boolean isEasyClue = false;
    private boolean isMediumClue = false;
    private boolean isHardClue = false;
    private boolean isEliteClue = false;
    private boolean isMasterClue = false;

    // cached boss booleans
    private boolean isKril = false;
    private boolean isKree = false;
    private boolean isZilyana = false;
    private boolean isZulrah = false;
    private boolean isHydra = false;
    private boolean isVorkath = false;
    private boolean isNightmare = false;

    // wilderness boss booleans
    private boolean isLowerTierWildernessBoss = false; // Chaos fanatic, Crazy archaeologist, Scorpia
    private boolean isHigherTierWildernessBoss = false; // Vet'ion, Venenatis, Callisto

    /*
     * Constructor for a DropTable that is from a source that is not an NPC.
     */

    public DropTable(){

        isNonNpcTable = true;

    }

    /*
     * Regular DropTable constructor
     */

    public DropTable(JsonArray jsonDrops, String npcName, DropSimulatorConfig config) throws IOException {

        this.preRollDrops = new ArrayList<>();
        this.alwaysDrops = new ArrayList<>();
        this.mainDrops = new ArrayList<>();
        this.tertiaryDrops = new ArrayList<>();
        this.catacombTertiaryDrops = new ArrayList<>();
        this.wildernessSlayerTertiaryDrops = new ArrayList<>();
        this.config = config;
        this.npcName = npcName;

        // Chaos Fanatic, Crazy archaeologist, and Scorpia are all lower tier wilderness bosses
        if(npcName.equalsIgnoreCase("Chaos Fanatic")
                || npcName.equalsIgnoreCase("Crazy archaeologist")
                || npcName.equalsIgnoreCase("Scorpia")){

            this.isLowerTierWildernessBoss = true;

        }

        // Vet'ion, Venenatis, and Callisto are all higher tier wilderness bosses
        if(npcName.equalsIgnoreCase("Vet'ion")
            || npcName.equalsIgnoreCase("Venenatis")
            || npcName.equalsIgnoreCase("Callisto")){

            this.isHigherTierWildernessBoss = true;

        }

        String wikiPage = "https://oldschool.runescape.wiki/w/" + npcName;
        Document doc = Jsoup.connect(wikiPage)
                .userAgent("RuneLite Drop Simulator")
                .get(); // connects to wikipedia page
        Elements tertiaryTable = doc.select("span#Tertiary"); // gets tertiary table
        Elements catacombsTable = doc.select("span#Catacombs_tertiary"); // gets catacombs table
        Elements preRollTable = doc.select("span#Pre-roll"); // gets pre-roll table

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

            // determine if the drop is a wilderness slayer drop
            try {
                myDrop.determineWildernessSlayer(wildernessSlayerTable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(myDrop.isTertiary()){ // if the drop is a tertiary drop

                this.tertiaryDrops.add(myDrop);

            } else if(myDrop.isPreRoll()){

                this.preRollDrops.add(myDrop);

            } else if (myDrop.isCatacomb()) {

                this.catacombTertiaryDrops.add(myDrop);

            } else if (myDrop.isWildernessSlayer()){

                this.wildernessSlayerTertiaryDrops.add(myDrop);

            } else if (myDrop.getRarity() == 1.0){

                this.alwaysDrops.add(myDrop);

            } else {

                if(isLowerTierWildernessBoss){

                    // if not an uncut sapphire and not a zamorak monk bottom
                    if(!myDrop.getName().equalsIgnoreCase("Uncut sapphire") &&
                            !myDrop.getName().equalsIgnoreCase("Zamorak monk bottom")){

                            this.mainDrops.add(myDrop);
                    }

                } else if(isHigherTierWildernessBoss){

                    // if not an uncut diamond and not a super restore(4)
                    if (!myDrop.getName().equalsIgnoreCase("Uncut diamond") &&
                            !myDrop.getName().equalsIgnoreCase("Super restore(4)")) {

                        this.mainDrops.add(myDrop);

                    }

                } else {

                    this.mainDrops.add(myDrop);

                }

            }

        }
    }

    /*
     * fillSpecialTable fills a table with drops that were acquired from a json file.
     */

    public void fillSpecialTable(ArrayList<Drop> alwaysDrops, ArrayList<Drop> preRoll, ArrayList<Drop> main, ArrayList<Drop> tertiary){

        this.preRollDrops = preRoll;
        this.alwaysDrops = alwaysDrops;
        this.mainDrops = main;
        this.tertiaryDrops = tertiary;
        this.catacombTertiaryDrops = new ArrayList();
        this.wildernessSlayerTertiaryDrops = new ArrayList();

    }

    /*
     * runTrials runs the drop trials and returns all of the simulated drops
     */

    public ArrayList<Drop> runTrials(int n){

        ArrayList<Drop> finalSimulatedDrops = new ArrayList();

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

        Random randy = new Random(); // random determines how many rolls clue scrolls will have

        int numPreRolls;
        int numRolls = 1; // assume monster has 1 roll

        rollAlwaysTable(emptyAlways,n); // always table only has one roll, no modification to n necessary

        finalSimulatedDrops.addAll(emptyAlways);

        // determine how many preRolls the monster has and then rolls accordingly
        if(!emptyPreRoll.isEmpty()){ // if there are preRoll drops

            if(isBarrows()) {
                numPreRolls = 7;
            } else if(isGrotGuardians()){
                numPreRolls = 2;
            } else {
                numPreRolls = emptyPreRoll.get(0).getRolls();
            }

            for(int i = 0; i < n*numPreRolls; i++){

                rollPreRollTable(emptyPreRoll, dropIntervalsPreRoll, preRollDrops);

            }
        }

        // determine how many rolls the monster has
        if(!emptyMain.isEmpty()) { // if there are main drops
            numRolls = emptyMain.get(0).getRolls();
        }

        // set number of rolls based on its non npc table
        if(isBeginnerClue()){
            numRolls = randy.nextInt(2) + 1; // beginner clue has 1-3 rolls
        } else if(isEasyClue()){
            numRolls = randy.nextInt(2) + 2; // easy clue has 2-4 rolls
        } else if(isMediumClue()){
            numRolls = randy.nextInt(2) + 3; // med clue has 3-5 rolls
        } else if(isHardClue() || isEliteClue()) {
            numRolls = randy.nextInt(2) + 4; // hard and elite clues have 4-6 rolls
        } else if(isMasterClue()){
            numRolls = randy.nextInt(2) + 5; // elite clue has 5-7 rolls
        } else if(isBarrows()){
            numRolls = 7;
        } else if(isTheatre()){
            numRolls = 3;
        } else if(isGrotGuardians()){
            numRolls = 2;
        } else if(isChambers()){
            numRolls = 2;
        } else if(isHydra()){
            numRolls = 2;
        }else if(isZulrah()){
            numRolls = 2;
        }else if(isVorkath()){
            numRolls = 2;
        }

        // number of Main Rolls depends on how many preroll drops were rolled
        int numMainRolls;

        // check if the drop table follows normal ordinance or if it is a special case table
        if(isTheatre()){ // if ToB

            numMainRolls = (n * numRolls) - preRolled*3;

        } else if(isChambers()) { // if CoX

            numMainRolls = (n * numRolls) - preRolled*2;

        } else { // otherwise if it is a normal drop table

            numMainRolls = (n * numRolls) - preRolled;
        }

        for(int i = 0; i < numMainRolls; i++) { // for n trials * num main rolls - numPreRolls

            rollTable(emptyMain, dropIntervalsMain, mainDrops);

        }

        // tertiary tables are only rolled once, so they are rolled with the trial number rather than number of rolls
        for(int i = 0; i < n; i ++) {

            rollTable(emptyTertiary, dropIntervalsTertiary, tertiaryDrops);

            // nonNpcTables do not need access to the config because none of thm have catacomb or wilderness drops

            if (!isNonNpcTable) { // only checks config if it is NOT a nonNpcTable

                if (config.catacombConfig()) {
                    rollTable(emptyCatacombs, dropIntervalsCatacombs, catacombTertiaryDrops);
                }

                if (config.wildernessConfig()) {
                    rollTable(emptyWilderness, dropIntervalsWilderness, wildernessSlayerTertiaryDrops);
                }

            }
        }

        finalSimulatedDrops.addAll(emptyPreRoll);

        // because some always drops are also main drops, it must be checked if the main drop has already been added
        // through the always drops

        ArrayList<Drop> toBeRemoved = new ArrayList<>();

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

        for(Drop d: toBeRemoved){
            emptyMain.remove(d);
        }

        finalSimulatedDrops.addAll(emptyMain);

        finalSimulatedDrops.addAll(emptyTertiary);

        finalSimulatedDrops.addAll(emptyCatacombs);

        finalSimulatedDrops.addAll(emptyWilderness);

        /*
         * Using coins as an example - if coins take up any number of drops on a drop table > 1, for example 3; the
         * Array list of drops will return the total dropped number of coins as 3 separate drops. For example, Nechryael
         * have 6 different coin drops. If the total number of dropped coins was 500k, the arraylist of drops will
         * have 6 different drops of coins all of which are 500k. The duplicates need to be removed from the list
         * leaving only the single correct 500k coin total. Also removes drops of quantity 0 - necessary because the
         * table was built upon all drops of the table starting with having a quantity of 0.
         */

        for (Drop d : finalSimulatedDrops) {

            int duplicate = 0;

            if(Integer.parseInt(d.getQuantity()) == 0){ // if it is an empty drop

                toBeRemoved.add(d);

            }

            for (Drop k : finalSimulatedDrops) {

                if (d.sameID(k)) { // if the drop appears multiple times
                    duplicate++;

                    if (duplicate > 1) { // if it paired with more than just itself
                        toBeRemoved.add(k);

                    }
                }
            }
        }

        for (Drop d : toBeRemoved) { // remove all duplicates and empty drops

            finalSimulatedDrops.remove(d);

        }

        // Because grotesque guardians have a drop of super combat (2), ranging potion (2), and magic potion (2) that
        // drop together, only super combats are rolled.

        if(isGrotGuardians()) { // grotesque guardians only method

            duplicateItem("Super combat potion(2)", "Magic potion(2)", 171, finalSimulatedDrops, 1.0);
            duplicateItem("Super combat potion(2)", "Ranging potion(2)", 3044, finalSimulatedDrops, 1.0);

        }

        // Because unsired drops bludgeon pieces in order and the plugin rolls a single chance of bludgeon piece, that
        // bludgeon piece must be turned into the three ordered pieces. Ex, if you rolled 10 bludgeon pieces, the drops
        // would be 4 claws, 3 spines, 3 axom.

        if(isUnsired()){ // unsired only method
            orderItems("Bludgeon claw", "Bludgeon spine", "Bludgeon axon", 13274, 13276, finalSimulatedDrops);
        }

        // Hydra rolls its hydra pieces in the exact same way that an unsired rolls bludgeon pieces
        if(isHydra()){
            orderItems("Hydra's eye", "Hydra's fang", "Hydra's heart", 22971,22969, finalSimulatedDrops);
        }

        // Kril drops super att 3, with super str 3 - and also drops super restore 3 with zammy brew 3
        if(isKril()) {

            duplicateItem("Super attack(3)", "Super strength(3)",157, finalSimulatedDrops, 1.0);
            duplicateItem("Zamorak brew(3)", "Super restore(3)",3026, finalSimulatedDrops, 1.0);

        }

        // Kree drops ranging potion 3 with super def 3
        if(isKree()){

            duplicateItem("Ranging potion(3)", "Super defence(3)",163, finalSimulatedDrops, 1.0);

        }

        // Zilyana drops super defence 3 with magic potion 3, and sara brew 3 with super restore 4
        if(isZilyana()){

            duplicateItem("Magic potion(3)", "Super defence(3)", 163, finalSimulatedDrops, 1.0);
            duplicateItem("Saradomin brew(3)", "Super restore(4)", 3024, finalSimulatedDrops, 1.0);

        }

        if(isLowerTierWildernessBoss()){

            duplicateItem("Zamorak monk top", "Zamorak monk bottom", 1033, finalSimulatedDrops, 1.0);
            duplicateItem("Uncut emerald", "Uncut sapphire",1623,finalSimulatedDrops,0.66666666666);
        }

        if(isHigherTierWildernessBoss()){

            duplicateItem("Dark crab", "Super restore(4)", 3024, finalSimulatedDrops, 0.375);
            duplicateItem("Uncut ruby", "Uncut diamond", 1617, finalSimulatedDrops, 0.5);
        }

        return finalSimulatedDrops;
    }

    /*
     * the rollAlwaysTable rolls the monsters guaranteed drop table
     */

    public void rollAlwaysTable(ArrayList<Drop> emptyTable, Integer n) {

        boolean guaranteedInterval = false;

        for (Drop alwaysDrop : this.alwaysDrops) { // for each 100% drop

            if (alwaysDrop.getQuantity().contains("-")) { // if one of the 100% drops is an interval drop

                guaranteedInterval = true;
                break;
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
        ArrayList<Double> dropIntervals = new ArrayList<>();

        dropIntervals.add(totalRarity);

        for (Drop tableDrop : tableDrops) { // for each main drop
            totalRarity += tableDrop.getRarity();
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

        ArrayList<Drop> clonedList = new ArrayList<>();

        for(Drop d: myList){

            clonedList.add(new Drop(d));

        }

        return clonedList;

    }


    /*
     * the rollPreRollTable drops rolls the monsters pre-roll drop table. If a pre-roll is dropped, the main roll is
     * skipped.
     */

    public void rollPreRollTable(ArrayList<Drop> emptyTable, ArrayList<Double> dropIntervals, ArrayList<Drop> tableDrops){
        Random randy = new Random();

        if(isChambers()){

            double points = 30000.0;
            double uniqueChance = points/8695;
            double chance = (100)*randy.nextDouble();

            if(chance > uniqueChance){ // if we have not rolled the unique table

                return; // do not roll the preroll table

            }

        }

        boolean nothing = false; // sets to true if the nothing drop is rolled

            double x = randy.nextDouble(); // rolls a random double between 0.0 and 1.0

            if(x >= dropIntervals.get(dropIntervals.size()-2)){ // if x is greater than the maximum probability
                nothing = true;
            }

            for (int j = 0; j < dropIntervals.size()-2; j++) { // for each drop in the drop interval

                if(nothing){
                    break;
                }

                if (x >= dropIntervals.get(j) && x <= dropIntervals.get(j+1)) { // finds the interval

                    Drop myDrop = tableDrops.get(j); // returns the drop at that interval
                    preRolled += 1; // increment the total number of prerolls dropped

                    int quantity;

                    if(myDrop.getQuantity().contains("-")){ // if interval quantity

                        String[] ranges = myDrop.getQuantity().split("-");
                        int lowerRange = Integer.parseInt(ranges[0]);
                        int higherRange = Integer.parseInt(ranges[1]);

                        quantity = randy.nextInt(higherRange - lowerRange) + lowerRange;

                    } else { // otherwise quantity remains normal

                        quantity = Integer.parseInt(myDrop.getQuantity());

                    }

                    for (Drop drop : emptyTable) { // runs through the empty table

                        if (myDrop.getId() == drop.getId()) { // if found

                            int currentQuantity = Integer.parseInt(drop.getQuantity());
                            int postQuantity = currentQuantity + quantity;

                            drop.setQuantity(Integer.toString(postQuantity));

                        }

                    }

                    break;
               }
            }

    }

    /*
     * rollTable rolls
     */

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

                    for (Drop drop : emptyTable) { // runs through the empty table

                        if (myDrop.getId() == drop.getId()) { // if found

                            int currentQuantity = Integer.parseInt(drop.getQuantity());
                            int postQuantity = currentQuantity + quantity;

                            drop.setQuantity(Integer.toString(postQuantity));

                        }

                    }

                    break;
                }
            }

        }

    }

    /*
     * Some monsters drop multiple of the same item on a single drop. ALl drop data considers the three drops to
     * have the same rarities, which causes the final total probability of all drops to be different than 1. To combat
     * this, the extra items are removed from the initial table, and duplicated here.
     */

    public void duplicateItem(String item, String itemToDuplicate, int dupeId, ArrayList<Drop> finalSimulatedDrops, double ratio){

        String numItem = "0";
        int itemIndex = 0; // gets index so display is prettier

        for(int i = 0; i < finalSimulatedDrops.size(); i++){
            if(finalSimulatedDrops.get(i).getName().equals(item)){ //

                numItem = finalSimulatedDrops.get(i).getQuantity();
                itemIndex = i;

            }
        }

        Double doubleNumItem = Double.parseDouble(numItem);
        doubleNumItem *=ratio;

        if(doubleNumItem>0) { // if there were instances of the item dropped

            finalSimulatedDrops.add(itemIndex + 1, // to the right
                    new Drop(dupeId, Integer.toString(doubleNumItem.intValue()), 1, 0.0, itemToDuplicate));
        }

    }

    /*
     * Some monsters drop one of a few different items in a specific order on the same drop roll chance. The simulation
     * initially only rolls the first item, and then orders them here.
     */

    public void orderItems(String item, String item2, String item3, int item2Id, int item3Id, ArrayList<Drop> finalSimulatedDrops){

        String numPieces;
        int itemIndex;

        for(int i = 0; i < finalSimulatedDrops.size(); i++) {
            if (finalSimulatedDrops.get(i).getName().equals(item)) { //
                numPieces = finalSimulatedDrops.get(i).getQuantity();
                itemIndex = i;

                if(Integer.parseInt(numPieces) > 1){  // if more than one of the item was rolled

                    int quotient = Integer.parseInt(numPieces) / 3;
                    int remainder = Integer.parseInt(numPieces) % 3;
                    int additive = 0;

                    if(remainder == 0) { // if remainder is 0, all 3 pieces have the same number of rolls

                        finalSimulatedDrops.get(itemIndex).setQuantity(Integer.toString(quotient));

                    } else if(remainder == 1){ // if remainder is 1, item has 1 more roll than the others

                        finalSimulatedDrops.get(itemIndex).setQuantity(Integer.toString(quotient + 1));

                    } else if(remainder == 2){ // if remainder is 2, initial item and item 2 have 1 more roll than item3

                        finalSimulatedDrops.get(itemIndex).setQuantity(Integer.toString(quotient + 1));
                        additive = 1;
                    }

                    finalSimulatedDrops.add(itemIndex + 1,
                            new Drop(item2Id, Integer.toString(quotient + additive), 1, 0, item2));
                    finalSimulatedDrops.add(itemIndex + 2,
                            new Drop(item3Id, Integer.toString(quotient), 1, 0, item3));

                    if(finalSimulatedDrops.get(itemIndex + 2).getQuantity().equals("0")){ // if 0 of item2

                        finalSimulatedDrops.remove(itemIndex + 2); // remove item 2

                    }

                }
            }
        }

    }

    /*
     * Getters and setters to specify unique tables with unique properties
     */

    public void setName(String name){
        this.npcName = name;
    }

    public String getName(){
        return this.npcName;
    }

    public boolean isTheatre(){
        return isTheatre;
    }

    public void setTheatre(boolean theatre){
        this.isTheatre = theatre;
    }

    public boolean isChambers(){
        return isChambers;
    }

    public void setChambers(boolean chambers){
        this.isChambers = chambers;
    }

    public boolean isGrotGuardians(){
        return isGrotGuardians;
    }

    public void setGrotGuardians(boolean grotGuardians){
        this.isGrotGuardians = grotGuardians;
    }

    public boolean isBeginnerClue() {
        return isBeginnerClue;
    }

    public void setBeginnerClue(boolean beginnerClue) {
        isBeginnerClue = beginnerClue;
    }

    public boolean isEasyClue() {
        return isEasyClue;
    }

    public void setEasyClue(boolean easyClue) {
        isEasyClue = easyClue;
    }

    public boolean isMediumClue() {
        return isMediumClue;
    }

    public void setMediumClue(boolean mediumClue) {
        isMediumClue = mediumClue;
    }

    public boolean isHardClue() {
        return isHardClue;
    }

    public void setHardClue(boolean hardClue) {
        isHardClue = hardClue;
    }

    public boolean isEliteClue() {
        return isEliteClue;
    }

    public void setEliteClue(boolean eliteClue) {
        isEliteClue = eliteClue;
    }

    public boolean isMasterClue() {
        return isMasterClue;
    }

    public void setMasterClue(boolean masterClue) {
        isMasterClue = masterClue;
    }

    public boolean isBarrows() {
        return isBarrows;
    }

    public void setBarrows(boolean barrows) {
        isBarrows = barrows;
    }

    public boolean isUnsired() {
        return isUnsired;
    }

    public void setUnsired(boolean unsired) {
        isUnsired = unsired;
    }

    public boolean isKril() {
        return isKril;
    }

    public void setKril(boolean kril) {
        isKril = kril;
    }

    public boolean isKree() {
        return isKree;
    }

    public void setKree(boolean kree) {
        isKree = kree;
    }

    public boolean isZilyana() {
        return isZilyana;
    }

    public void setZilyana(boolean zilyana) {
        isZilyana = zilyana;
    }

    public boolean isZulrah() {
        return isZulrah;
    }

    public void setZulrah(boolean zulrah) {
        isZulrah = zulrah;
    }

    public boolean isHydra() {
        return isHydra;
    }

    public void setHydra(boolean hydra) {
        isHydra = hydra;
    }

    public boolean isVorkath() {
        return isVorkath;
    }

    public void setVorkath(boolean vorkath) {
        isVorkath = vorkath;
    }

    public boolean isNightmare() {
        return isNightmare;
    }

    public void setNightmare(boolean nightmare) {
        isNightmare = nightmare;
    }

    public boolean isLowerTierWildernessBoss(){
       return isLowerTierWildernessBoss;
    }

    public boolean isHigherTierWildernessBoss(){
        return isHigherTierWildernessBoss;
    }

}
