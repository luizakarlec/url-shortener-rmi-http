# Distributed URL Shortener (Java RMI + HTTP)

An academic distributed systems project implementing a URL shortener in Java, using a hybrid architecture that combines **Java RMI** (for remote client-server communication when registering links) and **Java's built-in HTTP Server** for browser redirection.

## Features
- **Remote URL Shortening:** Sends an original URL from the client GUI and generates a unique positive hash code on the server via Java RMI
- **Shared In-Memory Storage:** Fast key-value mapping (Short Hash -> Original URL) accessed concurrently by both the RMI service and the HTTP server
- **Automatic Redirection (HTTP 302):** Intercepts browser requests on port `8080` and transparently redirects users to the target destination
- **Built-in Browser Testing:** Includes a direct button in the client window to open the generated short URL in the system's default web browser

## Technologies Used
- **Java SE** (JDK 8 or higher)
- **Java RMI** (`java.rmi`) — Remote Method Invocation
- **Java HTTP Server** (`com.sun.net.httpserver`) — Embedded web server for HTTP 302 redirection
- **Java Swing** (`javax.swing`) — Desktop graphical user interface for the client

## System Architecture
The server operates using a hybrid architecture, initializing two parallel services that share the same in-memory database instance (UrlDatabase):

#### Client
- `client.Client`: Desktop GUI responsible for capturing the user's input URL, looking up the remote object in the RMI registry, and opening the generated link in the browser
- `client.IGenerateUrl`: Remote interface defining the communication contract between client and server

#### Server
- `server.IGenerateUrl`: Remote interface defining the communication contract between client and server
- `server.GenerateUrl`: Remote service implementation that computes the URL hash code, stores the pair in the table, and returns the short URL
- `server.UrlDatabase`: In-memory data structure shared between both services
- `server.Server`: Server entry point, starts the RMI registry on port 1099 and launches the HTTP server on port 8080

## Installation and Execution

### Prerequisites
- Java JDK installed and configured
- Any Java IDE (NetBeans, IntelliJ IDEA, Eclipse, or VS Code)

### Step-by-Step
1. Clone the repository
```
git clone [https://github.com/luizakarlec/](https://github.com/luizakarlec/)[repository-name].git
cd [repository-name]
```

2. Start the Server
Open the project in your IDE and run the server.Server class

3. Start the Client
With the server running, execute the client.Client class to launch the graphical interface

4. Use the System

- Enter a full URL in the Insert URL field
- Click Generate to obtain the shortened link
- Click Test URL (or paste the link into your browser) to test the redirection
