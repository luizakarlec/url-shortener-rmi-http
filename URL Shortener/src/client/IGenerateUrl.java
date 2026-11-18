package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface that defines the contract for the URL shortening service.
 * Both the client and the server must share this exact interface so Java RMI
 * can invoke methods across the network.
 */
public interface IGenerateUrl extends Remote {
    
    /**
     * Receives an original long URL, stores it on the server, and returns a shortened URL.
     *
     * @param url The original long web address (e.g., "https://www.example.com").
     * @return The generated shortened URL (e.g., "http://localhost:8080/a1b2c").
     * @throws RemoteException If a network or RMI communication error occurs.
     */
    public String generateUrl(String url) throws RemoteException;
    
}
