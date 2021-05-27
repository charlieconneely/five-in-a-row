package Server.networking;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class WebServer {

    private static final int DEFAULT_PORT = 8081;
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String JOIN_ENDPOINT = "/join";

    private final int port;
    private HttpServer server;

    public WebServer (int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int serverPort = DEFAULT_PORT;
        // Port as cli argument.
        if (args.length == 1) serverPort = Integer.parseInt(args[0]);

        WebServer server = new WebServer(serverPort);
        server.startServer();

        System.out.println("Server is listening on port " + serverPort);
    }

    /**
     * Initializes server and sets up endpoints using HttpContext.
     * Starts server thread pool.
     */
    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch(IOException e) {
            e.printStackTrace();
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext joinContext = server.createContext(JOIN_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);

        // Connect endpoints to respective methods.
        statusContext.setHandler(this::handleStatusCheckRequest);
        joinContext.setHandler(this::handleJoinRequest);
        taskContext.setHandler(this::handleTaskRequest);

        // Create concurrent thread pool & start our server.
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    /**
     * Handles status requests from client.
     * @param exchange HttpExchange object.
     */
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        System.out.println("Called status endpoint.\n");

        String responseMessage = "Server is alive!\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void handleJoinRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        System.out.println("Called join endpoint.\n");

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(requestBytes);
        String responseMessage = String.format("Player %s has joined!\n", bodyString);
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        System.out.println("Called task endpoint.\n");

        // Retrieve request headers.
        Headers headers = exchange.getRequestHeaders();

        long startTime = System.nanoTime();
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = calculateResponse(requestBytes);
        long endTime = System.nanoTime();

        if (isDebugHeaderTrue(headers)) {
            String debugMessage = String.format("Operation took %d ns", endTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }

        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringNumbers = bodyString.split(",");

        BigInteger result = BigInteger.ONE;

        for (String number : stringNumbers) {
            BigInteger integer = new BigInteger(number);
            result = result.multiply(integer);
        }

        return String.format("Result of multiplication: %s\n", result).getBytes();
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

    private boolean isDebugHeaderTrue(Headers headers) {
        return (headers.containsKey("X-Debug")
                && headers.get("X-Debug").get(0).equalsIgnoreCase("true"));
    }
}