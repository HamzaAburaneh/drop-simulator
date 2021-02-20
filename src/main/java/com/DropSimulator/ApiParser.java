package com.DropSimulator;

import com.google.gson.*;

import net.runelite.api.Client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Scanner;

public class ApiParser {

    @Inject
    private Client client;

    // acquires the drop table of an npc using its id
    public JsonArray acquireDropTable(int id) throws IOException {

        String apiString = "https://api.osrsbox.com/monsters/" + String.valueOf(id);
        URL apiURL = new URL(apiString);
        HttpURLConnection conn = (HttpURLConnection)apiURL.openConnection();
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
    public ArrayList<Object> acquireDropTable(String npcName) throws IOException {

        // Not all monsters follow the same conventions in terms of capitalization in their names. In order to
        // combat this, the searched npc is searched on the oldschool wiki and then takes the title of the wiki as
        // the name. This gives us the exact name that will match the api, and allows the user to be less precise
        // when searching.


        String wikiString = "https://oldschool.runescape.wiki//w/api.php?action=opensearch&search=" + npcName + "&limit=10&format=json";

        Document doc = Jsoup.connect(wikiString).get(); // connects to wikipedia page
        String title = doc.title();
        String name = title.split("-")[0];
        String finalName = name.trim();
        name = name.trim();
        name = name.replace(" ", "%20"); // puts in form of API

        String apiString = "https://api.osrsbox.com/monsters?where={%20%22name%22%20:%20%22" + name + "%22%20}&projection={%20%22id%22:%201%20}";

        URL apiURL = new URL(apiString);
        HttpURLConnection conn = (HttpURLConnection)apiURL.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        String inline =" ";

        Scanner sc = new Scanner(apiURL.openStream());
        while(sc.hasNext()){
            inline+=sc.nextLine();
        }

        sc.close();

        String[] strings = inline.split("\"");
        int id = Integer.parseInt(strings[9]);

        ArrayList<Object> toBeReturned = new ArrayList<Object>();
        toBeReturned.add(acquireDropTable(id));
        toBeReturned.add(finalName);

        return toBeReturned;

    }
}
