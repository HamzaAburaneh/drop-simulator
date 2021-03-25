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

import com.google.gson.*;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseParser {

    private String userAgent = "RuneLite Drop Simulator";
    private DropSimulatorConfig config;

    public DatabaseParser(DropSimulatorConfig config){

        this.config = config;

    }

    /*
     * acquireWikiName acquires the name of a drop source searched in the search bar as it would appear if it were
     * searched on the wiki. Ex. searching grardor returns General Graardor. Not all monsters follow the same
     * conventions in terms of capitalization in their names. In order to combat this, the searched npc is searched on
     * the old school wiki and then takes the title of the wiki as the name. This gives us the exact name that will
     * match the api, and allows the user to be less precise when searching.
     */
    public String acquireWikiName(String searchedName) throws IOException {

        String wikiName;
        String wikiString = "https://oldschool.runescape.wiki//w/api.php?action=opensearch&search=" + searchedName + "&limit=10&format=json";

        Document doc = Jsoup.connect(wikiString)
                .userAgent(userAgent)
                .get(); // connects to wikipedia page

        String title = doc.title(); // Title is "NPC NAME - OSRS WIKI"
        String name = title.split("-")[0];  // removes - OSRS WIKI from title
        wikiName = name.trim(); // trims the name

        return wikiName;
    }

    /*
     * acquireDropTable with an integer argument acquires the JsonArray of the drops of NPC using its id
     */
    public JsonArray acquireDropTable(int id) throws IOException {

        String apiString = "https://api.osrsbox.com/monsters/" + id;

        String inline = Jsoup.connect(apiString)
                .userAgent(userAgent)
                .ignoreContentType(true)
                .execute()
                .body();

        JsonParser parser = new JsonParser();
        JsonObject jobj = (JsonObject)parser.parse(inline);
        JsonArray jsonarray = (JsonArray)jobj.get("drops");


        return jsonarray;

    }

    /*
     * acquireDropTable with a string argument acquires the drop table of any drop source using its name
     */
    public DropTable acquireDropTable(String dropSource, int id) throws IOException {

        long start = System.currentTimeMillis();

        DropTable searchedTable = new DropTable();
        boolean specialTable = false; // assumes table is not an npc table
        boolean bossTable = false; // assume the table is not a boss table
        ArrayList<String> quickSearches = new ArrayList();

        /*
         * The plugin initially searched a search on the wiki page and collected the title of the page of which it
         * connected. In order to speed up the search of non npc and boss tables, the Jaro Winkler Distance between a
         * search and the name of a special table determines which table was searched. If it is not a special table,
         * it will use the older method of getting the title from the wiki. Even though some of the special tables
         * have ID's, this method of skipping acquiring the title from the wiki significantly speeds up the search.
         * Obviously, this cannot be done for EVERY npc with a drop table, but it is not necessary. Bosses are
         * included in this section because bosses have tables that are more frequently searched.
         *
         * 1. Non-npc table searches are practically instant, no URL connections must be made because drops are included
         *    in local json files.
         * 2. Boss tables are very fast, but not as fast as non-npc tables. Bosses skip the acquire wiki name method
         *    but still need to connect to the osrs-box api in order to gather drops. Unless all bosses get their own
         *    json file, this is likely as fast as their simulations will be.
         * 3. Non special npc tables take an average of 1-2 seconds when using the search. They are much slower than
         *    the previous two table types, but are also less searched.
         */

        JaroWinklerDistance jaro = new JaroWinklerDistance();

        quickSearches.add("Beginner clue");
        quickSearches.add("Easy clue");
        quickSearches.add("Medium clue");
        quickSearches.add("Hard clue");
        quickSearches.add("Elite clue");
        quickSearches.add("Master clue");
        quickSearches.add("Theatre of Blood");
        quickSearches.add("Chambers of Xeric");
        quickSearches.add("Barrows");
        quickSearches.add("Unsired");
        quickSearches.add("Grotesque Guardians");
        quickSearches.add("General Graardor");
        quickSearches.add("K'ril Tsutsaroth");
        quickSearches.add("Commander Zilyana");
        quickSearches.add("Zilyana");
        quickSearches.add("Kree'arra");
        quickSearches.add("Zulrah");
        quickSearches.add("Kraken");
        quickSearches.add("Thermonuclear smoke devil");
        quickSearches.add("Cerberus");
        quickSearches.add("Sire");
        quickSearches.add("Alchemical Hydra");
        quickSearches.add("Demonic gorilla");
        quickSearches.add("Vorkath");
        quickSearches.add("Corporeal Beast");
        quickSearches.add("Scorpia");

        double maxJaro = 0;
        double jaroBound = 0.77;
        String maxStr = " ";

        // A right click simulation can be more precise, so if the id is not 0 (i.e., it is from a right click),
        // the jaroBound is higher

        if(id != 0){

            jaroBound = .90;

        }


        // Find the closest matching string using Jaro Winkler Distance. If the closest matching string has
        // a distance of < 0.66, assume that the string was not found.
        for(String str : quickSearches){

            if(jaro.apply(dropSource.toLowerCase(Locale.ROOT),str.toLowerCase(Locale.ROOT)) > maxJaro){

                maxJaro = jaro.apply(dropSource.toLowerCase(Locale.ROOT),str.toLowerCase(Locale.ROOT));

                if(maxJaro >= jaroBound) { // if the search matches somewhat strongly - not arbitrarily chosen. searching
                                           // hydra finds 'Hard clue' with .66 Jaro Winkler Distance.
                    maxStr = str;
                }
            }
        }


        /*
         * check for which string was found and set variables accordingly
         */

        if (maxStr.equals("Beginner clue")) {

            dropSource = "beginner_casket";
            searchedTable.setBeginnerClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Easy clue")) {

            dropSource = "easy_casket";
            searchedTable.setEasyClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Medium clue")) {

            dropSource = "medium_casket";
            searchedTable.setMediumClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Hard clue")) {

            dropSource = "hard_casket";
            searchedTable.setHardClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Elite clue")) {

            dropSource = "elite_casket";
            searchedTable.setEliteClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Master clue")) {

            dropSource = "master_casket";
            searchedTable.setMasterClue(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Theatre of Blood") || dropSource.equalsIgnoreCase("tob")) {

            dropSource = "theatre";
            searchedTable.setTheatre(true);
            searchedTable.setName("Theatre of Blood");
            specialTable = true;

        } else if (maxStr.equals("Chambers of Xeric") || dropSource.equalsIgnoreCase("cox")) {

            dropSource = "chambers";
            searchedTable.setChambers(true);
            searchedTable.setName("Chambers of Xeric");
            specialTable = true;

        } else if (maxStr.equals("Barrows")) {

            dropSource = "barrows_chest";
            searchedTable.setBarrows(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Unsired")) {

            dropSource = "unsired";
            searchedTable.setUnsired(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Grotesque Guardians")) {

            dropSource = "grotesque_guardians";
            searchedTable.setGrotGuardians(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("General Graardor") || dropSource.equalsIgnoreCase("Bandos") || dropSource.equals("graardor") || dropSource.equals("grardor")) {

            dropSource = "graardor";
            searchedTable.setName("General Graardor");
            specialTable = true;

        } else if (maxStr.equals("Kree'arra") || dropSource.equalsIgnoreCase("Armadyl") || dropSource.equalsIgnoreCase("Arma")) {

            dropSource = "kree'arra";
            searchedTable.setKree(true);
            searchedTable.setName("Kree'arra");
            specialTable = true;

        } else if (maxStr.equals("Commander Zilyana") || dropSource.equalsIgnoreCase("Saradomin") || dropSource.equalsIgnoreCase("Sara") || maxStr.equals("Zilyana")) {

            dropSource = "zilyana";
            searchedTable.setZilyana(true);
            searchedTable.setName("Commander Zilyana");
            specialTable = true;

        } else if (maxStr.equals("K'ril Tsutsaroth") || dropSource.equalsIgnoreCase("Zamorak") || dropSource.equalsIgnoreCase("Zammy")) {

            dropSource = "k'ril";
            searchedTable.setKril(true);
            searchedTable.setName("K'ril Tsutsaroth");
            specialTable = true;

        } else if (maxStr.equals("Zulrah")) {

            dropSource = "zulrah";
            searchedTable.setZulrah(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Kraken")) {

            dropSource = "kraken";
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Thermonuclear smoke devil") || dropSource.equalsIgnoreCase("thermy")) {

            dropSource = "thermonuclear_smoke_devil";
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Cerberus")) {

            dropSource = "cerberus";
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (dropSource.equalsIgnoreCase("Abyssal Sire") || maxStr.equals("Sire")) {

            dropSource = "abyssal_sire";
            searchedTable.setName("Abyssal Sire");
            specialTable = true;

        } else if (maxStr.equals("Alchemical Hydra")) {

            dropSource = "alchemical_hydra";
            searchedTable.setHydra(true);
            searchedTable.setName(maxStr);
            specialTable = true;

        } else if (maxStr.equals("Demonic gorilla")) {

            dropSource = maxStr;
            searchedTable.setName(maxStr);
            bossTable = true;

        } else if (maxStr.equals("Vorkath")) {

            dropSource = maxStr;
            searchedTable.setVorkath(true);
            searchedTable.setName(maxStr);
            bossTable = true;

        } else if (maxStr.equals("Corporeal Beast") || dropSource.equalsIgnoreCase("Corp")) {

            dropSource = "Corporeal Beast";
            searchedTable.setName(maxStr);
            bossTable = true;


    }

        if(specialTable){ // if a special table

            ArrayList<Object> subTables;
            subTables = acquireNonNpcTable(dropSource);
            searchedTable.fillNonNpcTable(
                    (ArrayList<Drop>)subTables.get(0),
                    (ArrayList<Drop>)subTables.get(1),
                    (ArrayList<Drop>)subTables.get(2),
                    (ArrayList<Drop>)subTables.get(3));

        } else if(bossTable){ // if a boss table

            // skip acquiring the wiki name in order to increase speed

            if(id != 0){ // if the method is being called from a right click menu

                searchedTable = new DropTable(acquireDropTable(id), dropSource, config);

            } else { // if the method is not being called from a right click menu

                searchedTable = acquireNpcDropTable(dropSource);

            }

        } else { // if not a special table


            if(id != 0) { // if the method is being called from a right click menu

                searchedTable = new DropTable(acquireDropTable(id), dropSource, config);

            } else {

                dropSource = acquireWikiName(dropSource); // still kinda slow, but works
                searchedTable = acquireNpcDropTable(dropSource);

            }

        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println((double)timeElapsed/1000);

        return searchedTable;

    }

    /*
     * acquireNpcDropTable acquires the drop table of an NPC using its name
     */
    public DropTable acquireNpcDropTable(String dropSource) throws IOException {

        String name = dropSource.replace(" ", "%20"); // puts in form of API

        String apiString = "https://api.osrsbox.com/monsters?where={%20%22name%22%20:%20%22" + name + "%22%20}&projection={%20%22id%22:%201%20}";

        String inline = Jsoup.connect(apiString)
                .userAgent(userAgent)
                .ignoreContentType(true)
                .execute()
                .body();

        String[] strings = inline.split("\"");
        int id = Integer.parseInt(strings[9]);

        DropTable searchedTable = new DropTable(acquireDropTable(id),dropSource,config);

        return searchedTable;
    }

    /*
     * acquireNonNpcTable returns the subtables of a drop table of a non NPC by using its name. It returns subtables
     * preRolls, main, etc. rather than the drop table itself because a boolean also needs to be set specifying
     * which table it is. By returning just the subtables, a table can be filled with the subtables that already
     * has the correct booleans set.
     */
    public ArrayList<Object> acquireNonNpcTable(String dropSource) throws UnsupportedEncodingException {

        ArrayList<Object> subTables = new ArrayList(); // all sub tables to be returned in an array list of objects
        ArrayList<Drop> alwaysDrops = new ArrayList();
        ArrayList<Drop> preRollDrops = new ArrayList();
        ArrayList<Drop> mainDrops = new ArrayList();
        ArrayList<Drop> tertiaryDrops = new ArrayList();

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(dropSource + ".json");
        JsonParser parser = new JsonParser();
        JsonObject jobj = (JsonObject) parser.parse(new InputStreamReader(in,"UTF-8"));
        JsonArray jsonarray = (JsonArray)jobj.get("data");

        for(int i = 0; i < jsonarray.size(); i++){

            JsonObject myObj = (JsonObject) jsonarray.get(i);

            int id = myObj.get("id").getAsInt();
            String quantity = myObj.get("quantity").toString().replace("\"","");
            double rarity = myObj.get("rarity").getAsDouble();
            String name = myObj.get("name").getAsString().replace("\"","");
            String type = myObj.get("drop-type").getAsString().replace("\"","");


            if(type.equals("pre-roll")){ // if pre-roll

                preRollDrops.add(new Drop(id,quantity,1,rarity,name));

            } else if(type.equals("always")){ // if always

                alwaysDrops.add(new Drop(id,quantity,1,rarity,name));

            } else if(type.equals("tertiary")){ // if tertiary

                tertiaryDrops.add(new Drop(id,quantity,1,rarity,name));

            } else { // otherwise it is a main drop

                mainDrops.add(new Drop(id,quantity,1,rarity,name));

            }

        }

        subTables.add(alwaysDrops);
        subTables.add(preRollDrops);
        subTables.add(mainDrops);
        subTables.add(tertiaryDrops);

        return subTables;
    }
}
