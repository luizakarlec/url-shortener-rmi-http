package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main server class that initializes and coordinates both the RMI service
 * (for registering shortened URLs) and the built-in HTTP server (for browser redirection).
 * Both services share the same in-memory {@link UrlDatabase} instance.
 */
public class Server {

    static String enderecoServidor = "localhost";
    static String HOST_URL = "rmi://" + enderecoServidor + "/GenerateUrl";
    static UrlDatabase db = new UrlDatabase();
    
    /**
     * Application entry point. Starts the RMI registry and the HTTP redirect server.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String args[]) {
        try {
            iniciarRMI();
            iniciarHTTP();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    /**
     * Starts the Java RMI registry on the default port (1099) and binds the
     * {@link GenerateUrl} remote object so clients can register new URLs.
     *
     * @throws Exception If the RMI registry cannot be created or bound.
     */
    static void iniciarRMI() throws Exception {
        try {
            // Create the RMI registry on port 1099
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            
            // Instantiate the remote object sharing the in-memory database
            GenerateUrl objetoRemoto = new GenerateUrl(enderecoServidor, db);
            
            // Register the remote object under the specified RMI URL
            Naming.bind(HOST_URL, objetoRemoto);
            System.out.println("RMI Server online");
        } catch (RemoteException e) {
            System.out.println("Error: " + e);
        }
    }
    
    /**
     * Starts Java's built-in HTTP server on port 8080 to handle
     * incoming browser requests and redirect shortened URLs.
     *
     * @throws Exception If the HTTP server fails to bind to port 8080.
     */
    static void iniciarHTTP() throws Exception {
        // Create a lightweight built-in HTTP server listening on port 8080
        HttpServer serverHTTP = HttpServer.create(new InetSocketAddress(8080), 0); //servidor web leve embutido no java

        // Map the root context ("/") to intercept all incoming requests
        serverHTTP.createContext("/", new RedirectHandler());
        
        // Use the default single-threaded executor and start the server
        serverHTTP.setExecutor(null);
        serverHTTP.start();
        System.out.println("Web HTTP server online at " + enderecoServidor);
    }
    
    /**
     * HTTP request handler responsible for processing browser visits,
     * looking up the short hash code in the shared database, and issuing
     * an HTTP 302 redirect (or 404 if not found).
     */
    static class RedirectHandler implements HttpHandler {
        
        /**
         * Handles an incoming HTTP request from a web browser.
         *
         * @param exchange Encapsulates the HTTP request details and response methods.
         * @throws IOException If an I/O error occurs while sending the response.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Extract the requested path (e.g., "/24234") and remove the leading slash
            String path = exchange.getRequestURI().getPath();
            String codigo = path.substring(1); 

            // Look up the original URL in the shared HashMap using the extracted code
            String urlOriginal = db.urls.get(codigo);

            if (urlOriginal != null) {
                // Found: set the "Location" header and return HTTP 302 (Temporary Redirect)
                exchange.getResponseHeaders().set("Location", urlOriginal);
                exchange.sendResponseHeaders(302, -1); // -1 indicates no response body
                System.out.println("Redirecting " + codigo + " to " + urlOriginal);
            } else {
                // Not found: return an HTTP 404 error page
                String respostaErro = "<h1>404 - Shortened link not found!</h1>";
                exchange.sendResponseHeaders(404, respostaErro.length());
                exchange.getResponseBody().write(respostaErro.getBytes());
            }
            
            // Close the exchange to complete the HTTP transaction
            exchange.close();
        }
    }
}
