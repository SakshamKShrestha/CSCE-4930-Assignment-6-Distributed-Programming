
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RestClient {

    public static void main(String[] args) {
        String keyword = args.length > 0 ? args[0] : "money";

        try {
            String encodedKeyword = URLEncoder.encode(
                keyword, StandardCharsets.UTF_8
            );

            String url = "http://localhost:8080/search?keyword="
                    + encodedKeyword;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("HTTP Status: " + response.statusCode());
            System.out.println("Search Keyword: " + keyword);
            System.out.println("Server Response:");
            System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("REST request failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
