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

/*
 * the NonNpcDropTables class holds all data regarding drop sources that do not have a monster id.
 * E.g. Theatre of Blood drop table, barrows drop table. Since the drop table data from the osrsbox api does not contain
 * drop tables from non npc drop tables, and the wiki page of each of these varies greatly, these tables are
 * manually input. Future update - would like for these tables to scrape the wiki in order to build the tables. The
 * current tables were small but for tables such as clue scrolls, it will be necessary to automate.
 */


import java.util.ArrayList;

public class NonNpcDropTables {

    ArrayList<String> nonNpcTableNames = new ArrayList();
    ArrayList<String> clueTables = new ArrayList();

    public NonNpcDropTables(){

        // all nonNpc tables that the parser will check for
        nonNpcTableNames.add("Theatre of Blood");
        nonNpcTableNames.add("Chambers of Xeric");
        nonNpcTableNames.add("Barrows");
        nonNpcTableNames.add("Unsired");
        nonNpcTableNames.add("Grotesque Guardians");
        nonNpcTableNames.add("Clue scroll (beginner)");
        nonNpcTableNames.add("Clue scroll (easy)");
        nonNpcTableNames.add("Clue scroll (medium)");
        nonNpcTableNames.add("Clue scroll (hard)");
        nonNpcTableNames.add("Clue scroll (elite)");
        nonNpcTableNames.add("Clue scroll (master)");

        // all clue tables are nonNpc tables, but not all nonNpc tables are clue tables
        clueTables.add("Clue scroll (beginner)");
        clueTables.add("Clue scroll (easy)");
        clueTables.add("Clue scroll (medium)");
        clueTables.add("Clue scroll (hard)");
        clueTables.add("Clue scroll (elite)");
        clueTables.add("Clue scroll (master)");

    }

    public DropTable createNonNpcDropTable(String name) {

        DropTable nonNpcTable = new DropTable();

        /*
         * ToB
         */

        if(name.equals("Theatre of Blood")) {

            nonNpcTable.setTheatre(true);

            // ToB has no 100% drops
            ArrayList<Drop> alwaysDrops = new ArrayList();

            // theatre pre roll drops
            ArrayList<Drop> preRollDrops = new ArrayList();
            preRollDrops.add(new Drop(22477, "1", 1, 0.04629629629, "Avernic defender hilt"));
            preRollDrops.add(new Drop(22324, "1", 1, 0.01156737998, "Ghrazi rapier"));
            preRollDrops.add(new Drop(22481, "1", 1, 0.01156737998, "Sanguinesti staff (uncharged)"));
            preRollDrops.add(new Drop(22326, "1", 1, 0.01156737998, "Justiciar faceguard"));
            preRollDrops.add(new Drop(22327, "1", 1, 0.01156737998, "Justiciar chestguard"));
            preRollDrops.add(new Drop(22328, "1", 1, 0.01156737998, "Justiciar legguards"));
            preRollDrops.add(new Drop(22486, "1", 1, 0.00578368999, "Scythe of vitur (uncharged)"));

            // theatre main drops
            ArrayList<Drop> mainDrops = new ArrayList();
            mainDrops.add(new Drop(22446, "50-60", 3, 0.06666666666, "Vial of blood"));
            mainDrops.add(new Drop(560, "500-600", 3, 0.03333333333, "Death rune"));
            mainDrops.add(new Drop(565, "500-600", 3, 0.03333333333, "Blood rune"));
            mainDrops.add(new Drop(1939, "500-600", 3, 0.03333333333, "Swamp tar"));
            mainDrops.add(new Drop(453, "500-600", 3, 0.03333333333, "Coal"));
            mainDrops.add(new Drop(444, "300-360", 3, 0.03333333333, "Gold ore"));
            mainDrops.add(new Drop(1775, "200-240", 3, 0.03333333333, "Molten glass"));
            mainDrops.add(new Drop(449, "130-156", 3, 0.03333333333, "Adamantite ore"));
            mainDrops.add(new Drop(451, "60-72", 3, 0.03333333333, "Runite ore"));
            mainDrops.add(new Drop(245, "50-60", 3, 0.03333333333, "Wine of zamorak"));
            mainDrops.add(new Drop(3138, "50-60", 3, 0.03333333333, "Potato cactus"));
            mainDrops.add(new Drop(215, "50-60", 3, 0.03333333333, "Grimy cadantine"));
            mainDrops.add(new Drop(211, "40-48", 3, 0.03333333333, "Grimy avantoe"));
            mainDrops.add(new Drop(3049, "37-44", 3, 0.03333333333, "Grimy toadflax"));
            mainDrops.add(new Drop(213, "36-43", 3, 0.03333333333, "Grimy kwuarm"));
            mainDrops.add(new Drop(209, "34-40", 3, 0.03333333333, "Grimy irit leaf"));
            mainDrops.add(new Drop(207, "30-36", 3, 0.03333333333, "Grimy ranarr weed"));
            mainDrops.add(new Drop(3051, "27-32", 3, 0.03333333333, "Grimy snapdragon"));
            mainDrops.add(new Drop(2485, "26-31", 3, 0.03333333333, "Grimy lantadyme"));
            mainDrops.add(new Drop(217, "24-28", 3, 0.03333333333, "Grimy dwarf weed"));
            mainDrops.add(new Drop(219, "20-24", 3, 0.03333333333, "Grimy torstol"));
            mainDrops.add(new Drop(1391, "15-18", 3, 0.03333333333, "Battlestaff"));
            mainDrops.add(new Drop(21488, "8-12", 3, 0.03333333333, "Mahogany seed"));
            mainDrops.add(new Drop(1373, "4", 3, 0.03333333333, "Rune battleaxe"));
            mainDrops.add(new Drop(1127, "4", 3, 0.03333333333, "Rune platebody"));
            mainDrops.add(new Drop(1113, "4", 3, 0.03333333333, "Rune chainbody"));
            mainDrops.add(new Drop(5289, "3", 3, 0.03333333333, "Palm tree seed"));
            mainDrops.add(new Drop(5315, "3", 3, 0.03333333333, "Yew seed"));
            mainDrops.add(new Drop(5316, "3", 3, 0.03333333333, "Magic seed"));

            // theatre tertiary drops
            ArrayList<Drop> tertiaryDrops = new ArrayList();
            tertiaryDrops.add(new Drop(12073, "1", 1, 0.12000480019, "Clue scroll (elite)"));
            tertiaryDrops.add(new Drop(22473, "1", 1, 0.00153846153, "Lil 'zik"));

            nonNpcTable.fillNonNpcTable(alwaysDrops,preRollDrops,mainDrops,tertiaryDrops);

            /*
             * Barrows
             */

        } else if(name.equals("Barrows")) {

            // barrows has no 100% drops
            ArrayList<Drop> alwaysDrops = new ArrayList();

            // pre roll drops
            ArrayList<Drop> preRollDrops = new ArrayList();

            // ahrim's
            preRollDrops.add(new Drop(4708,"1",7,0.00040849673,"Ahrim's hood"));
            preRollDrops.add(new Drop(4712,"1",7,0.00040849673,"Ahrim's robetop"));
            preRollDrops.add(new Drop(4714,"1",7,0.00040849673,"Ahrim's robeskirt"));
            preRollDrops.add(new Drop(4710,"1",7,0.00040849673,"Ahrim's staff"));
            // dharok's
            preRollDrops.add(new Drop(4716,"1",7,0.00040849673,"Dharok's helm"));
            preRollDrops.add(new Drop(4720,"1",7,0.00040849673,"Dharok's platebody"));
            preRollDrops.add(new Drop(4722,"1",7,0.00040849673,"Dharok's platelegs"));
            preRollDrops.add(new Drop(4718,"1",7,0.00040849673,"Dharok's greataxe"));
            // guthan's
            preRollDrops.add(new Drop(4724,"1",7,0.00040849673,"Guthan's helm"));
            preRollDrops.add(new Drop(4728,"1",7,0.00040849673,"Guthan's platebody"));
            preRollDrops.add(new Drop(4730,"1",7,0.00040849673,"Guthan's chainskirt"));
            preRollDrops.add(new Drop(4726,"1",7,0.00040849673,"Guthan's warspear"));
            // karil's
            preRollDrops.add(new Drop(4732,"1",7,0.00040849673,"Karil's coif"));
            preRollDrops.add(new Drop(4736,"1",7,0.00040849673,"Karil's leathertop"));
            preRollDrops.add(new Drop(4738,"1",7,0.00040849673,"Karil's leatherskirt"));
            preRollDrops.add(new Drop(4734,"1",7,0.00040849673,"Karil's crossbow"));
            // torag's
            preRollDrops.add(new Drop(4745,"1",7,0.00040849673,"Torag's helm"));
            preRollDrops.add(new Drop(4749,"1",7,0.00040849673,"Torag's platebody"));
            preRollDrops.add(new Drop(4751,"1",7,0.00040849673,"Torag's platelegs"));
            preRollDrops.add(new Drop(4747,"1",7,0.00040849673,"Torag's hammers"));
            // verac's
            preRollDrops.add(new Drop(4753,"1",7,0.00040849673,"Verac's helm"));
            preRollDrops.add(new Drop(4757,"1",7,0.00040849673,"Verac's brassard"));
            preRollDrops.add(new Drop(4759,"1",7,0.00040849673,"Verac's plateskirt"));
            preRollDrops.add(new Drop(4755,"1",7,0.00040849673,"Verac's flail"));

            // main drops
            ArrayList<Drop> mainDrops = new ArrayList();
            mainDrops.add(new Drop(995,"2-774",7,0.3745318352,"Coins"));
            mainDrops.add(new Drop(558,"380-504",7,0.1235177866,"Mind rune"));
            mainDrops.add(new Drop(562,"168-209",7,0.1235177866,"Chaos rune"));
            mainDrops.add(new Drop(560,"105-125",7,0.1235177866,"Death rune"));
            mainDrops.add(new Drop(565,"56-65",7,0.1235177866,"Blood rune"));
            mainDrops.add(new Drop(4740,"35-40",7,0.1235177866,"Bolt rack"));
            mainDrops.add(new Drop(987,"1",7,0.00296471983,"Loop half of key"));
            mainDrops.add(new Drop(985,"1",7,0.00296471983,"Tooth half of key"));
            mainDrops.add(new Drop(1149,"1",7,0.00098814229,"Dragon med helm"));

            // tertiary drops
            ArrayList<Drop> tertiaryDrops = new ArrayList();
            tertiaryDrops.add(new Drop(	12073,"1",7,0.005,"Clue scroll (elite)"));

            nonNpcTable.fillNonNpcTable(alwaysDrops,preRollDrops,mainDrops,tertiaryDrops);

            /*
             * Unsired
             */

        } else if (name.equals("Unsired")){

            // unsired has no 100% drops
            ArrayList<Drop> alwaysDrops = new ArrayList();

            // unsired has no pre roll drops
            ArrayList<Drop> preRollDrops = new ArrayList();

            // unsired main drops
            ArrayList<Drop> mainDrops = new ArrayList();
            mainDrops.add(new Drop(18628,"1",1,0.48426150121,"Bludgeon piece"));
            mainDrops.add(new Drop(13265,"1",1,0.20312817387,"Abyssal dagger"));
            mainDrops.add(new Drop(4151,"1",1,0.09372071227,"Abyssal whip"));
            mainDrops.add(new Drop(13277,"1",1,0.10156408693,"Jar of miasma"));
            mainDrops.add(new Drop(7979,"1",1,0.078125,"Abyssal head"));
            mainDrops.add(new Drop(13262,"1",1,0.0390625,"Abyssal orphan"));

            // unsired has no tertiary drops
            ArrayList<Drop> tertiaryDrops = new ArrayList();

            nonNpcTable.fillNonNpcTable(alwaysDrops,preRollDrops,mainDrops,tertiaryDrops);

            /*
             * CoX
             */

        } else if (name.equals("Chambers of Xeric")){

            nonNpcTable.setChambers(true);

            // CoX has no 100% drops
            ArrayList<Drop> alwaysDrops = new ArrayList();

            // preroll drops
            ArrayList<Drop> preRollDrops = new ArrayList();
            preRollDrops.add(new Drop(21079, "1",1, 0.2899, "Arcane prayer scroll"));
            preRollDrops.add(new Drop(21034, "1",1, 0.2899, "Dexterous prayer scroll"));
            preRollDrops.add(new Drop(21000, "1",1, 0.058, "Twisted buckler"));
            preRollDrops.add(new Drop(21012, "1",1, 0.058, "Dragon hunter crossbow"));
            preRollDrops.add(new Drop(21015, "1",1, 0.0435, "Dinh's bulwark"));
            preRollDrops.add(new Drop(21018, "1",1, 0.0435, "Ancestral hat"));
            preRollDrops.add(new Drop(21021, "1",1, 0.0435, "Ancestral robe top"));
            preRollDrops.add(new Drop(21024, "1",1, 0.0435, "Ancestral robe bottom"));
            preRollDrops.add(new Drop(13652, "1",1, 0.0435, "Dragon claws"));
            preRollDrops.add(new Drop(21003, "1",1, 0.029, "Elder maul"));
            preRollDrops.add(new Drop(21043, "1",1, 0.029, "Kodai insignia"));
            preRollDrops.add(new Drop(20997, "1",1, 0.029, "Twisted bow"));

            // main drops
            ArrayList<Drop> mainDrops = new ArrayList();

            mainDrops.add(new Drop(560,"833",2,0.0303030303, "Death rune"));
            mainDrops.add(new Drop(565,"937",2,0.0303030303, "Blood rune"));
            mainDrops.add(new Drop(566,"1500",2,0.0303030303, "Soul rune"));
            mainDrops.add(new Drop(892,"2142",2,0.0303030303, "Rune arrow"));
            mainDrops.add(new Drop(11212,"148",2,0.0303030303, "Dragon arrow"));
            mainDrops.add(new Drop(207,"38",2,0.0303030303, "Grimy ranarr weed"));
            mainDrops.add(new Drop(3049,"57",2,0.0303030303, "Grimy toadflax"));
            mainDrops.add(new Drop(209,"185",2,0.0303030303, "Grimy irit leaf"));
            mainDrops.add(new Drop(211,"92",2,0.0303030303, "Grimy avantoe"));
            mainDrops.add(new Drop(213,"79",2,0.0303030303, "Grimy kwuarm"));
            mainDrops.add(new Drop(3051,"23",2,0.0303030303, "Grimy snapdragon"));
            mainDrops.add(new Drop(215,"90",2,0.0303030303, "Grimy cadantine"));
            mainDrops.add(new Drop(2485,"120",2,0.0303030303, "Grimy lantadyme"));
            mainDrops.add(new Drop(217,"150",2,0.0303030303, "Grimy dwarf weed"));
            mainDrops.add(new Drop(219,"37",2,0.0303030303, "Grimy torstol"));
            mainDrops.add(new Drop(442,"1500",2,0.0303030303, "Silver ore"));
            mainDrops.add(new Drop(453,"1500",2,0.0303030303, "Coal"));
            mainDrops.add(new Drop(444,"681",2,0.0303030303, "Gold ore"));
            mainDrops.add(new Drop(447,"937",2,0.0303030303, "Mithril ore"));
            mainDrops.add(new Drop(449,"180",2,0.0303030303, "Adamantite ore"));
            mainDrops.add(new Drop(451,"15",2,0.0303030303, "Runite ore"));
            mainDrops.add(new Drop(1623,"159",2,0.0303030303, "Uncut sapphire"));
            mainDrops.add(new Drop(1621,"211",2,0.0303030303, "Uncut emerald"));
            mainDrops.add(new Drop(1619,"123",2,0.0303030303, "Uncut ruby"));
            mainDrops.add(new Drop(1617,"59",2,0.0303030303, "Uncut diamond"));
            mainDrops.add(new Drop(13391,"1071",2,0.0303030303, "Lizardman fang"));
            mainDrops.add(new Drop(7936,"15000",2,0.0303030303, "Pure essence"));
            mainDrops.add(new Drop(13421,"1250",2,0.0303030303, "Saltpetre"));
            mainDrops.add(new Drop(8780,"312",2,0.0303030303, "Teak plank"));
            mainDrops.add(new Drop(8782,"126",2,0.0303030303, "Mahogany plank"));
            mainDrops.add(new Drop(13573,"555",2,0.0303030303, "Dynamite"));
            mainDrops.add(new Drop(21047,"1",2,0.0303030303, "Torn prayer scroll"));
            mainDrops.add(new Drop(21027,"1",2,0.0303030303, "Dark relic"));


            // tertiary drops
            ArrayList<Drop> tertiaryDrops = new ArrayList();
            tertiaryDrops.add(new Drop(12073,"1",1,0.08333333333,"Clue scroll (elite)"));
            tertiaryDrops.add(new Drop(20851,"1",1,0.00065283019,"Olmlet"));

            nonNpcTable.fillNonNpcTable(alwaysDrops,preRollDrops,mainDrops,tertiaryDrops);

            /*
             * Grotesque Guardians
             */

        } else if (name.equals("Grotesque Guardians")){

            nonNpcTable.setGrotGuardians(true);

            // 100% drops
            ArrayList<Drop> alwaysDrops = new ArrayList();
            alwaysDrops.add(new Drop(21726,"50-100", 1, 1.0, "Granite dust"));

            // pre roll drops
            ArrayList<Drop> preRollDrops = new ArrayList();
            alwaysDrops.add(new Drop(4153,"1", 2, 0.004, "Granite maul"));
            alwaysDrops.add(new Drop(21736,"1", 2, 0.002, "Granite gloves"));
            alwaysDrops.add(new Drop(21739,"1", 2, 0.002, "Granite ring"));
            alwaysDrops.add(new Drop(21742,"1", 2, 0.001333333, "Granite hammer"));
            alwaysDrops.add(new Drop(21730,"1", 2, 0.001, "Black tourmaline core"));

            // main drops
            ArrayList<Drop> mainDrops = new ArrayList();
            mainDrops.add(new Drop(1275,"1",2,0.04380201489,"Rune pickaxe"));
            mainDrops.add(new Drop(1163,"1",2,0.03649635036,"Rune full helm"));
            mainDrops.add(new Drop(1079,"1",2,0.03649635036,"Rune platelegs"));
            mainDrops.add(new Drop(1319,"1",2,0.02919708029,"Rune 2h sword"));
            mainDrops.add(new Drop(1373,"1",2,0.02189621195,"Rune battleaxe"));
            mainDrops.add(new Drop(1305,"1",2,0.00729927007,"Dragon longsword"));
            mainDrops.add(new Drop(4129,"1",2,0.00729927007,"Adamant boots"));
            mainDrops.add(new Drop(1149,"1",2,0.00729927007,"Dragon med helm"));
            mainDrops.add(new Drop(7058,"4-6",2,0.07299270072,"Mushroom potato"));
            mainDrops.add(new Drop(6685,"2",2,0.05780346820,"Saradomin brew(4)"));
            mainDrops.add(new Drop(12699,"1",2,0.04380201489,"Super combat potion(2)"));
            mainDrops.add(new Drop(141,"1-2",2,0.02919708029,"Prayer potion(2)"));
            mainDrops.add(new Drop(444,"40-75",2,0.05109862033,"Gold ore"));
            mainDrops.add(new Drop(2361,"25-40",2,0.04380201489,"Adamantite bar"));
            mainDrops.add(new Drop(453,"180-250",2,0.04380201489,"Coal"));
            mainDrops.add(new Drop(2357,"37-50",2,0.04380201489,"Gold bar"));
            mainDrops.add(new Drop(2359,"35-45",2,0.04380201489,"Mithril bar"));
            mainDrops.add(new Drop(451,"3-6",2,0.02919708029,"Runite ore"));
            mainDrops.add(new Drop(2363,"3-5",2,0.02189621195,"Runite bar"));
            mainDrops.add(new Drop(995,"10000-20000",2,0.07299270072,"Coins"));
            mainDrops.add(new Drop(562,"100-150",2,0.05837711617,"Chaos rune"));
            mainDrops.add(new Drop(995,"25000",2,0.03649635036,"Coins"));
            mainDrops.add(new Drop(989,"1",2,0.03649635036,"Crystal key"));
            mainDrops.add(new Drop(560,"60-100",2,0.03649635036,"Death rune"));
            mainDrops.add(new Drop(11232,"15-25",2,0.02919708029,"Dragon dart tip"));
            mainDrops.add(new Drop(9192,"100-150",2,0.02189621195,"Diamond bolt tips"));
            mainDrops.add(new Drop(9193,"20-40",2,0.01459854014,"Dragonstone bolt tips"));
            mainDrops.add(new Drop(9194,"4-10",2,0.01459854014,"Onyx bolt tips"));
            mainDrops.add(new Drop(11237,"50-150",2,0.00729927007,"Dragon arrowtips"));

            // tertiary drops
            ArrayList<Drop> tertiaryDrops = new ArrayList();
            tertiaryDrops.add(new Drop(23083,"1",1,0.02325581395,"Brimstone key"));
            tertiaryDrops.add(new Drop(12073,"1",1,0.00434782608,"Clue scroll (elite)"));
            tertiaryDrops.add(new Drop(21748,"1",1,0.00033333333,"Noon"));
            tertiaryDrops.add(new Drop(21745,"1",1,0.0002,"Jar of stone"));
            nonNpcTable.fillNonNpcTable(alwaysDrops,preRollDrops,mainDrops,tertiaryDrops);
        }

        return nonNpcTable;

    }

}
