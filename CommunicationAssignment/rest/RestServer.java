
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class RestServer {

    private static final int PORT = 8080;
    private static final Path INDEX_PATH = Paths.get("index", "Index.txt");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(PORT), 0
        );

        server.createContext("/search", RestServer::handleSearch);
        server.start();

        System.out.println("REST server running on port " + PORT);
        System.out.println("Endpoint: http://localhost:8080/search?keyword=money");
    }

    private static void handleSearch(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getRawQuery();
        String keyword = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);

                if (pair.length == 2 &&
                    pair[0].equals("keyword")) {
                    keyword = URLDecoder.decode(
                        pair[1], StandardCharsets.UTF_8
                    );
                }
            }
        }

        if (keyword == null || keyword.isBlank()) {
            sendResponse(exchange, 400, "Missing keyword");
            return;
        }

        String result = searchTermInIndex(keyword);

        if (result.isEmpty()) {
            sendResponse(exchange, 404, "Keyword not found");
        } else {
            sendResponse(exchange, 200, result);
        }
    }

    public static String searchTermInIndex(String keyword)
            throws IOException {

        try (BufferedReader reader =
                 Files.newBufferedReader(INDEX_PATH)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\.\\|");

                if (parts.length > 0 &&
                    parts[0].equals(keyword)) {
                    return line;
                }
            }
        }

        return "";
    }

    private static void sendResponse(
            HttpExchange exchange, int status, String message)
            throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
