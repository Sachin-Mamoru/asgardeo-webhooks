package org.petcaredev;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;

@SuppressWarnings("checkstyle:JavadocType")
public class LoginEventListener {

    @SuppressWarnings("checkstyle:Regexp")
    public static void main(String[] args) throws IOException {
        // Create a server that listens on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Set a handler for the /login path
        server.createContext("/login-event", new LoginEventHandler());

        // Start the server
        server.start();

        System.out.println("Server is listening on port 8000/n");

        new ChoreoWebSubHubSubscriber().subscribeToWebSubHub(
            "https://hub.websubhub.choreo.dev/hub",
            "petcaredev-LOGINS",
            "https://d09c399e-0aad-43c3-ae21-b3b53f86368a-dev.e1-us-east-azure.choreoapis.dev/prit/petcaredeveventhookjava/login-event-listener-be2/v1.0/login-event"
        );
    }

    static class LoginEventHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the body of the HTTP request
            InputStream requestBody = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            String line;
            StringBuilder body = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            // Output the body to the console
            System.out.println(body.toString());

            // Send a response
            String response = "Received";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}