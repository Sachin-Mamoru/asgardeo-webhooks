package org.petcaredev;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("checkstyle:JavadocType")
public class LoginEventListener {

    public static ChoreoWebSubHubSubscriber choreoWebSubHubSubscriber;
    private static final String hubUrl = "https://hub.websubhub.choreo.dev/hub";
    private static final String topicUrl = "petcaredev-LOGINS";
    private static final String callbackUrl =
            "https://d09c399e-0aad-43c3-ae21-b3b53f86368a-dev.e1-us-east-azure.choreoapis.dev/prit/petcaredeveventhookjava/login-event-listener-be2/v1.0/login-event";

    @SuppressWarnings("checkstyle:Regexp")
    public static void main(String[] args) throws IOException {
        // Create a server that listens on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Set a handler for the /login path
        server.createContext("/login-event", new LoginEventHandler());

        // Start the server
        server.start();

        System.out.println("Server is listening on port 8000/n");

        choreoWebSubHubSubscriber = new ChoreoWebSubHubSubscriber();
        choreoWebSubHubSubscriber.subscribeToWebSubHub(hubUrl, topicUrl, callbackUrl);
    }

    static class LoginEventHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            System.out.println("Received a request." + "Method: " + exchange.getRequestMethod() + " Path: " +
                    exchange.getRequestURI().getPath() + " Query: " + exchange.getRequestURI().getQuery() +
                    " Headers: " + exchange.getRequestHeaders() + " Body: " + exchange.getRequestBody());
            // Check if this is a GET request or a POST request
            if (exchange.getRequestMethod().equals("GET")) {

                URI requestURI = exchange.getRequestURI();
                String query = requestURI.getQuery();
                Map<String, String> parameters = queryToMap(query);

                System.out.println(
                        "Received a GET request. Checking for hub.mode, hub.topic, and hub.challenge parameters.");
                String hubMode = parameters.get("hub.mode");
                String hubTopic = parameters.get("hub.topic");
                String hubChallenge = parameters.get("hub.challenge");

                if (hubMode != null && hubTopic != null && hubChallenge != null) {
                    System.out.println("hub.mode: " + hubMode);
                    System.out.println("hub.topic: " + hubTopic);
                    System.out.println("hub.challenge: " + hubChallenge);
                    if ("subscribe".equals(hubMode) && topicUrl.equals(hubTopic)) {
                        // Send hub.challenge back as response
                        exchange.sendResponseHeaders(200, hubChallenge.length());
                        exchange.getResponseBody().write(hubChallenge.getBytes());
                        exchange.close();
                        return;
                    }
                }
                // Send a 404 response
                exchange.sendResponseHeaders(404, hubChallenge.length());
                exchange.getResponseBody().write(hubChallenge.getBytes());
                exchange.close();
                return;
            }

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
            String response = "Event Received";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }

        private Map<String, String> queryToMap(String query) {

            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
            return result;
        }
    }

}