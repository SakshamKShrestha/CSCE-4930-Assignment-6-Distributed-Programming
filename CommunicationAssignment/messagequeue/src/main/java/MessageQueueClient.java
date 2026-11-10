
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class MessageQueueClient {

    private static final String BASE_URL = "http://localhost:8081";

    public static void main(String[] args) throws Exception {

        String keyword = args.length > 0 ? args[0] : "money";

        HttpClient client = HttpClient.newHttpClient();

        
        String encodedKeyword = URLEncoder.encode(
                keyword, StandardCharsets.UTF_8
        );

        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL + "/search?keyword=" + encodedKeyword
                ))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> submitResponse = client.send(
                submitRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        if (submitResponse.statusCode() != 202) {
            System.err.println(
                    "Search submission failed: " + submitResponse.body()
            );
            return;
        }

        String requestId = submitResponse.body().trim();

        System.out.println("Search keyword: " + keyword);
        System.out.println("Request ID: " + requestId);
        System.out.println("Request submitted to RabbitMQ.");

        
        for (int attempt = 1; attempt <= 30; attempt++) {

            HttpRequest resultRequest = HttpRequest.newBuilder()
                    .uri(URI.create(
                            BASE_URL + "/results?id=" + requestId
                    ))
                    .GET()
                    .build();

            HttpResponse<String> resultResponse = client.send(
                    resultRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (resultResponse.statusCode() == 200) {
                System.out.println("\nSearch completed!");
                System.out.println("Server response:");
                System.out.println(resultResponse.body());
                return;
            }

            if (resultResponse.statusCode() != 202) {
                System.err.println(
                        "Unexpected response: " + resultResponse.body()
                );
                return;
            }

            System.out.println("Attempt " + attempt + ": PENDING");
            Thread.sleep(1000);
        }

        System.out.println("Timed out waiting for search results.");
    }
}
