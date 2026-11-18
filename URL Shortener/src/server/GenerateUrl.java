package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * Remote object implementation for the URL shortening service.
 * Extends {@link UnicastRemoteObject} so clients can invoke its methods remotely via Java RMI.
 */
public class GenerateUrl extends UnicastRemoteObject implements IGenerateUrl {

    String url;
    UrlDatabase db;
    String serverAddress;

    /**
     * Constructs a new GenerateUrl remote service.
     *
     * @param serverAddress The host address where the HTTP redirect server is running (e.g., "localhost").
     * @param db            The shared in-memory database used to store the shortened URLs.
     * @throws RemoteException If an error occurs while exporting the remote object.
     */
    public GenerateUrl(String serverAddress, UrlDatabase db) throws RemoteException {
        this.db = db;
        this.serverAddress = serverAddress;
    }

    /**
     * Generates a unique positive hash code for the given URL, stores it in the database,
     * and returns the complete shortened HTTP link.
     *
     * @param url The original long URL to be shortened.
     * @return The shortened URL pointing to the local HTTP redirect server.
     * @throws RemoteException If a remote communication error occurs.
     */
    public String generateUrl(String url) throws RemoteException {
        this.url = url;
        
        // Generate a positive hash code from the URL (0x7fffffff removes the negative sign bit)
        String hash = String.valueOf(url.hashCode() & 0x7fffffff);

        // Save the mapping in the shared database if it does not exist yet
        if (db.urls.get(hash) == null) {
            db.urls.put(hash, url);
        }

        // Print all stored URLs in the server console for debugging
        System.out.println("Displaying the URLs hash table!");        
        for (Map.Entry<String, String> entry : db.urls.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // Build and return the final shortened URL pointing to port 8080
        String shortUrl = "http://" + this.serverAddress + ":8080/" + hash;

        return shortUrl;
    }   
}