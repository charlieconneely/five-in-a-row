package Server.networking;

import Server.GameManager;
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
    private static final String STATUS_ENDPOINT = "/status";
    private static final String JOIN_ENDPOINT = "/join";
    private static final String GAME_STATE_ENDPOINT = "/state";
    private static final String PLAYER_MOVE_ENDPOINT = "/move";

    private final int port;
    private HttpServer server;
    private GameManager gameManager;

    public WebServer(int port) {
        this.port = port;
        gameManager = new GameManager();
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
        HttpContext stateContext = server.createContext(GAME_STATE_ENDPOINT);
        HttpContext moveContext = server.createContext(PLAYER_MOVE_ENDPOINT);

        // Connect endpoints to respective methods.
        statusContext.setHandler(this::handleStatusCheckRequest);
        joinContext.setHandler(this::handleJoinRequest);
        stateContext.setHandler(this::handleGameStateCheckRequest);
        moveContext.setHandler(this::handlePlayerMoveRequest);

        // Create concurrent thread pool & start our server.
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    /**
     * Handles server status requests from client.
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

    private void handleGameStateCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        System.out.println("Called game state endpoint.\n");
        addGameStateToHeaders(exchange);
        String boardState = gameManager.getBoardStateAsText();
        sendResponse(boardState.getBytes(), exchange);
    }

    private void handlePlayerMoveRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        System.out.println("Called move endpoint.\n");
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(requestBytes);
        int columnChoice = Integer.parseInt(bodyString);

        gameManager.handlePlayerMove(columnChoice-1);
    }

    private void handleJoinRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        String responseMessage = "";
        System.out.println("Called join endpoint.\n");

        if (gameIsFull()) {
            responseMessage = "Sorry, the game is full.";
            sendResponse(responseMessage.getBytes(), exchange);
            return;
        }

        // Get bytes array containing name from request body.
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        // Convert to String.
        String bodyString = new String(requestBytes);
        gameManager.addPlayer(bodyString);
        responseMessage = String.format("Player %s has joined!\nAll players: %s.\n",
                bodyString, gameManager.getPlayers());
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

    private void addGameStateToHeaders(HttpExchange exchange) {
        // Get the name of player whose turn it is.
        String name = gameManager.getPlayerTurn();
        String waiting = String.valueOf(!gameIsFull());
        exchange.getResponseHeaders().put("X-Player-Turn", Arrays.asList(name));
        exchange.getResponseHeaders().put("X-Waiting", Arrays.asList(waiting));
    }

    private boolean gameIsFull() {
        return (gameManager.numberOfPlayers() == 2);
    }
}
