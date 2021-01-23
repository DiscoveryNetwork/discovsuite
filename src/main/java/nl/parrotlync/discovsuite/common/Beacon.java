package nl.parrotlync.discovsuite.common;

import java.net.HttpURLConnection;
import java.net.URL;

public class Beacon {

    public static boolean authenticate() {
        try {
            URL url = new URL("https://ipictserver.nl/beacon/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) { return true; }
        } catch (Exception ignored) { }
        return false;
    }
}
