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
     * Sends asynchronous requests to the HTTP server.
     *
     * @param url Server address
     * @param requestPayload Message data
     * @return CompletableFuture<String> Http Response
     */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        HttpRequest request = createHttpPostRequest(url, requestPayload);
        // Send our request asynchronously and extract body from HTTP response.
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    public CompletableFuture<HttpResponse<String>> sendGameStateCheck(String url) {
        HttpRequest request = createHttpGetRequest(url);
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<String> sendStatusCheck(String url) {
        HttpRequest request = createHttpGetRequest(url);
        // Send our request asynchronously and extract body from HTTP response.
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

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
    public void sendShutDownRequest(String url, byte[] requestPayload) {
        HttpRequest request = createHttpPostRequest(url, requestPayload);
        System.out.println("Shutting down...");
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
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
