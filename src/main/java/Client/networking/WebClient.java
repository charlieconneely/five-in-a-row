package Client.networking;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Handles our HTTP communication with the server.
 */
public class WebClient {

    private HttpClient httpClient;

    public WebClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    /**
     * Sends HTTP POST request to url endpoint and returns the Response body.
     *
     * @param url Server address
     * @param requestPayload Message data
     * @return CompletableFuture<String> Http Response
     */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        HttpRequest request = createHttpPostRequest(url, requestPayload);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    /**
     * Send HTTP GET request to /state enpoint.
     *
     * @param url Server address
     * @return HttpResponse containing headers with latest information about the game state.
     */
    public CompletableFuture<HttpResponse<String>> sendGameStateCheck(String url) {
        HttpRequest request = createHttpGetRequest(url);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Sends HTTP GET request to /status endpoint.
     *
     * @param url Server address
     * @return String 'Server is alive' confirmation message
     */
    public CompletableFuture<String> sendStatusCheck(String url) {
        HttpRequest request = createHttpGetRequest(url);
        // Send our request asynchronously and extract body from HTTP response.
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    /**
     * Sends HTTP POST request to /move endpoint.
     *
     * @param url Server address
     * @param requestPayload Byte array representing the user chosen column
     */
    public void sendMove(String url, byte[] requestPayload) {
        HttpRequest request = createHttpPostRequest(url, requestPayload);
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    /**
     * Sends synchronous POST request to the server on client shutdown.
     *
     * @param url Server address
     * @param requestPayload Message data
     */
    public void sendShutDownRequest(String url, byte[] requestPayload) throws IOException, InterruptedException {
        HttpRequest request = createHttpPostRequest(url, requestPayload);
        System.out.println("Shutting down...");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[CLIENT]" + response.body());
    }

    /**
     * Create and parameterize a HTTP POST request.
     *
     * @param url Server address
     * @param requestPayload Message data
     * @return HttpRequest
     */
    private HttpRequest createHttpPostRequest(String url, byte[] requestPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .build();
        return request;
    }

    /**
     * Create and parameterize a HTTP GET request.
     *
     * @param url Server address
     * @return HttpRequest
     */
    private HttpRequest createHttpGetRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        return request;
    }
}
