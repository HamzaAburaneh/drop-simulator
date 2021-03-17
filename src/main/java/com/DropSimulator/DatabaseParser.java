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

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseParser {

    private String userAgent = "RuneLite Drop Simulator";

    // acquires the drop table of an npc using its id
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

    // acquires the drop table of an npc using its name
    // returns 2 objects - a json array of drops, and final string name
    // unless it is a nonNpc table, in which case it returns that it is a nonNpc table and the type
    public ArrayList<Object> acquireDropTable(String npcName) throws IOException {

        ArrayList<Object> toBeReturned = new ArrayList();

        // Not all monsters follow the same conventions in terms of capitalization in their names. In order to
        // combat this, the searched npc is searched on the oldschool wiki and then takes the title of the wiki as
        // the name. This gives us the exact name that will match the api, and allows the user to be less precise
        // when searching.

        String wikiString = "https://oldschool.runescape.wiki//w/api.php?action=opensearch&search=" + npcName + "&limit=10&format=json";

        Document doc = Jsoup.connect(wikiString)
                .userAgent(userAgent)
                .get(); // connects to wikipedia page
        String title = doc.title(); // Title is "NPC NAME - OSRS WIKI"

        NonNpcDropTables nonNpcTables = new NonNpcDropTables();

        String name = title.split("-")[0];  // removes - OSRS WIKI from title
        String finalName = name.trim();           // trims title
        name = name.trim();

        if(nonNpcTables.nonNpcTableNames.contains(name)){ // if it is a nonNpc table

            toBeReturned.add("nonNpcTable");
            toBeReturned.add(name);

        } else {

            name = name.replace(" ", "%20"); // puts in form of API

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

            toBeReturned.add(acquireDropTable(id));
            toBeReturned.add(finalName);

        }

        return toBeReturned;

    }

    // returns the drop table of a clue scroll
    public DropTable acquireClueTable(String dropSource) throws SQLException {

        DropTable clueTable = new DropTable();
        String source = null;
        Connection con = DriverManager.getConnection("jdbc:sqlite::resource:non_npc_tables.sqlite");
        Statement stat = con.createStatement();

        if(dropSource.equals("Clue scroll (beginner)")) {

            source = "'Beginner casket'";
            clueTable.setBeginnerClue(true);

        } else if(dropSource.equals("Clue scroll (easy)")) {

            source = "'Easy casket'";
            clueTable.setEasyClue(true);

        } else if(dropSource.equals("Clue scroll (medium)")){

            source = "'Medium casket'";
            clueTable.setMediumClue(true);

        } else if(dropSource.equals("Clue scroll (hard)")){

            source = "'Hard casket'";
            clueTable.setHardClue(true);

        } else if(dropSource.equals("Clue scroll (elite)")){

            source = "'Elite casket'";
            clueTable.setEliteClue(true);

        } else if(dropSource.equals("Clue scroll (master)")){

            source = "'Master casket'";
            clueTable.setMasterClue(true);

        }

        String query = ("select * from " + source); // select all from clue table
        ResultSet rs = stat.executeQuery(query);

        ArrayList<Drop> alwaysDrops = new ArrayList();
        ArrayList<Drop> preRollDrops = new ArrayList();
        ArrayList<Drop> mainDrops = new ArrayList();
        ArrayList<Drop> tertiaryDrops = new ArrayList();

        while(rs.next()){

            int ID = Integer.parseInt(rs.getString("ID"));
            String quantity = rs.getString("Quantity");
            double rarity = rs.getDouble("Rarity");
            String name = rs.getString("Item");

            if(name.contains("Clue scroll")){ // if it is a clue scroll
                tertiaryDrops.add(new Drop(ID,quantity,1,rarity,name)); // it is a tertiary drop
            } else { // otherwise
                mainDrops.add(new Drop(ID, quantity, 1, rarity, name)); // main drop
            }

        }

        clueTable.fillNonNpcTable(alwaysDrops, preRollDrops, mainDrops, tertiaryDrops);

        con.close();

        return clueTable;
    }
}
