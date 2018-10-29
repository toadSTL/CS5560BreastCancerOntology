import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BioportalAPI {

    static final String REST_URL = "http://data.bioontology.org";
    static final String API_KEY = "ec511abb-8761-41a6-a094-e6f931afa672";
    static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, String> getClassDetails(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";

        Map<String,String> ret = new HashMap<String, String>();

        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();

            JsonNode classDetails  = jsonToNode(result);

            if(classDetails != null)
            {
                ret.put(classDetails.get("prefLabel").toString(), classDetails.get("links").get("ontology").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void slow(){
        final long INTERVAL = 100000000;
        long start = System.nanoTime();
        long end=0;
        do{
            end = System.nanoTime();
        }while(start + INTERVAL >= end);
        //System.out.println("Waited: " + (end - start));
    }
}