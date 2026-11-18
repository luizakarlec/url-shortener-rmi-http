package server;

import java.util.HashMap;

/**
 * In-memory database shared between the RMI server and the HTTP server.
 * Stores the mapping between generated short hash codes and their original URLs.
 */
public class UrlDatabase {
    
    /**
     * Map storing the URL pairs:
     * Key = short hash code (e.g., "24234")
     * Value = original long URL (e.g., "https://...")
     */
    public HashMap<String,String> urls;

    /**
     * Initializes an empty in-memory HashMap to store the URLs.
     */
    public UrlDatabase() {
        urls = new HashMap<>();
    }
}