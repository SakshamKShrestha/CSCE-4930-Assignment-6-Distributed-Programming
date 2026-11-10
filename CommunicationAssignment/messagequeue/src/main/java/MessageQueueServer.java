
import com.rabbitmq.client.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageQueueServer {

    public static final String QUEUE_NAME = "document_search_queue";

    public static final Map<String, String> results =
            new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8081), 0
        );

        
        server.createContext("/search", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String query = exchange.getRequestURI().getRawQuery();
            String keyword = getParameter(query, "keyword");

            if (keyword == null || keyword.isBlank()) {
                sendResponse(exchange, 400, "Missing keyword");
                return;
            }

            String requestId = UUID.randomUUID().toString();

            
            String message = requestId + "|" + keyword;

            try {
                synchronized (channel) {
                    channel.basicPublish(
                            "",
                            QUEUE_NAME,
                            null,
                            message.getBytes(StandardCharsets.UTF_8)
                    );
                }

                System.out.println("Queued request: " + message);
                sendResponse(exchange, 202, requestId);

            } catch (Exception e) {
                sendResponse(exchange, 500, "Queue error: " + e.getMessage());
            }
        });

        
        server.createContext("/results", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String query = exchange.getRequestURI().getRawQuery();
            String requestId = getParameter(query, "id");

            if (requestId == null || requestId.isBlank()) {
                sendResponse(exchange, 400, "Missing request ID");
                return;
            }

            if (results.containsKey(requestId)) {
                sendResponse(exchange, 200, results.get(requestId));
            } else {
                sendResponse(exchange, 202, "PENDING");
            }
        });
        MessageQueueWorker.start();
        
        server.start();

        System.out.println("Message Queue HTTP server running on port 8081");
        System.out.println("POST /search?keyword=money");
        System.out.println("GET /results?id=<request-id>");
    }

    private static String getParameter(String query, String key) {
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);

            if (pair.length == 2 && pair[0].equals(key)) {
                return URLDecoder.decode(
                        pair[1], StandardCharsets.UTF_8
                );
            }
        }

        return null;
    }

    private static void sendResponse(
            HttpExchange exchange, int status, String message)
            throws IOException {

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
