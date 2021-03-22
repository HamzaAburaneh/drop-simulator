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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Scanner;

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
        URL apiURL = new URL(apiString);
        HttpURLConnection conn = (HttpURLConnection)apiURL.openConnection();
        conn.addRequestProperty("User-Agent", userAgent);
        conn.setRequestMethod("GET");
        conn.connect();

        String inline =" ";

        Scanner sc = new Scanner(apiURL.openStream());
        while(sc.hasNext()){
            inline+=sc.nextLine();
        }

        sc.close();

        JsonParser parser = new JsonParser();
        JsonObject jobj = (JsonObject)parser.parse(inline);
        JsonArray jsonarray = (JsonArray)jobj.get("drops");

        return jsonarray;

    }

    /*
     * acquireDropTable with a string argument acquires the drop table of any drop source using its name
     */
    public DropTable acquireDropTable(String dropSource) throws IOException {

        DropTable searchedTable = new DropTable();
        boolean nonNpcTable = false; // assumes table is not an npc table

        /*
         * Because users might search either the clue scroll itself OR its respective casket, both searches
         * need to work for a clue. Thus, if it is either the clue or casket drop, it is directed towards the casket
         * .json file.
         */

        if (dropSource.equals("Clue scroll (beginner)") || dropSource.equals("Reward casket (beginner)")) {

            dropSource = "beginner_casket";
            searchedTable.setBeginnerClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Clue scroll (easy)") || dropSource.equals("Reward casket (easy)")) {

            dropSource = "easy_casket";
            searchedTable.setEasyClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Clue scroll (medium)") || dropSource.equals("Reward casket (medium)")) {

            dropSource = "medium_casket";
            searchedTable.setMediumClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Clue scroll (hard)") || dropSource.equals("Reward casket (hard)")) {

            dropSource = "hard_casket";
            searchedTable.setHardClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Clue scroll (elite)") || dropSource.equals("Reward casket (elite)")) {

            dropSource = "elite_casket";
            searchedTable.setEliteClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Clue scroll (master)") || dropSource.equals("Reward casket (master)")) {

            dropSource = "master_casket";
            searchedTable.setMasterClue(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Theatre of Blood")) {

            dropSource = "theatre";
            searchedTable.setTheatre(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Chambers of Xeric")) {

            dropSource = "chambers";
            searchedTable.setChambers(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Barrows") || dropSource.equals("Chest_(Barrows)")) {

            dropSource = "barrows_chest";
            searchedTable.setBarrows(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Unsired")) {

            dropSource = "unsired";
            searchedTable.setUnsired(true);
            nonNpcTable = true;

        } else if (dropSource.equals("Grotesque Guardians")){

            dropSource = "grotesque_guardians";
            searchedTable.setGrotGuardians(true);
            nonNpcTable = true;

        }

        if(nonNpcTable){ // if a non npc table

            ArrayList<Object> subTables;
            subTables = acquireNonNpcTable(dropSource);
            searchedTable.fillNonNpcTable(
                    (ArrayList<Drop>)subTables.get(0),
                    (ArrayList<Drop>)subTables.get(1),
                    (ArrayList<Drop>)subTables.get(2),
                    (ArrayList<Drop>)subTables.get(3));

        } else { // if an npc table

            searchedTable = acquireNpcDropTable(dropSource);

        }

        return searchedTable;

    }

    /*
     * acquireNpcDropTable acquires the drop table of an NPC using its name
     */
    public DropTable acquireNpcDropTable(String dropSource) throws IOException {

        String name = dropSource.replace(" ", "%20"); // puts in form of API

        String apiString = "https://api.osrsbox.com/monsters?where={%20%22name%22%20:%20%22" + name + "%22%20}&projection={%20%22id%22:%201%20}";

        URL apiURL = new URL(apiString);
        HttpURLConnection conn = (HttpURLConnection) apiURL.openConnection();
        conn.addRequestProperty("User-Agent", userAgent);
        conn.setRequestMethod("GET");
        conn.connect();

        String inline = " ";

        Scanner sc = new Scanner(apiURL.openStream());
        while (sc.hasNext()) {
            inline += sc.nextLine();
        }

        sc.close();

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
