package Server.networking;

import Server.GameManager;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.Executors;

/**
 * Handles our HTTP communication with the client.
 */
public class WebServer {

    private static final int DEFAULT_PORT = 8082;
    private static final String STATUS_ENDPOINT = "/status";
    private static final String JOIN_ENDPOINT = "/join";
    private static final String GAME_STATE_ENDPOINT = "/state";
    private static final String PLAYER_MOVE_ENDPOINT = "/move";
    private static final String PLAYER_QUIT_ENDPOINT = "/quit";

    private final int port;
    private HttpServer server;
    GameManager gameManager = new GameManager();

    public WebServer(int port) {
        this.port = port;
        setGameManager(gameManager);
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
        setupHttpContextObjects();
        // Create concurrent thread pool & start our server.
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void setupHttpContextObjects() {
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext joinContext = server.createContext(JOIN_ENDPOINT);
        HttpContext stateContext = server.createContext(GAME_STATE_ENDPOINT);
        HttpContext moveContext = server.createContext(PLAYER_MOVE_ENDPOINT);
        HttpContext quitContext = server.createContext(PLAYER_QUIT_ENDPOINT);

        // Connect endpoints to respective methods.
        statusContext.setHandler(this::handleStatusCheckRequest);
        joinContext.setHandler(this::handleJoinRequest);
        stateContext.setHandler(this::handleGameStateCheckRequest);
        moveContext.setHandler(this::handlePlayerMoveRequest);
        quitContext.setHandler(this::handleQuitRequest);
    }

    /**
     * Handles server status requests from client.
     *
     * @param exchange HttpExchange object.
     */
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        System.out.println("[SERVER] /status endpoint called.\n");
        String responseMessage = "Server is alive!\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    /**
     * Handles requests on the /state endpoint.
     * Retrieves state-representing data from the GameManager.
     * Adds information to the Http Response as custom headers.
     *
     * @param exchange HttpExchange object
     */
    private void handleGameStateCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        System.out.println("[SERVER] /state endpoint called.\n");
        addGameStateToHeaders(exchange);
        String boardState = gameManager.getBoardStateAsText();
        sendResponse(boardState.getBytes(), exchange);
    }

    /**
     * Handles requests to the /move endpoint.
     * Extracts client's column choice from request body and
     * passes information to the GameManager.
     *
     * @param exchange HttpExchange object
     */
    private void handlePlayerMoveRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        System.out.println("[SERVER] /move endpoint called.\n");

        String playerMove = getStringFromRequestBody(exchange);
        int columnChoice = Integer.parseInt(playerMove);
        gameManager.handlePlayerMove(columnChoice-1);
        sendResponse(new byte[0], exchange);
    }

    /**
     * Handles requests to the /join endpoint.
     * Adds client to the game if game is not full.
     *
     * @param exchange HttpExchange object
     */
    private void handleJoinRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        System.out.println("[SERVER] /join endpoint called.\n");
        String responseMessage = "";

        if (gameIsFull()) {
            responseMessage = "Sorry, the game is full.";
            sendResponse(responseMessage.getBytes(), exchange);
            return;
        }
        String clientName = getStringFromRequestBody(exchange);
        gameManager.addPlayer(clientName);
        responseMessage = String.format("\nAll players: %s\n", gameManager.getPlayers());
        sendResponse(responseMessage.getBytes(), exchange);
    }

    /**
     * Handles requests made to the /quit endpoint.
     *
     * @param exchange HttpExchange object
     */
    private void handleQuitRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        System.out.println("[SERVER] /quit endpoint called.\n");

        String clientName = getStringFromRequestBody(exchange);
        gameManager.removePlayer(clientName);
        addGameStateToHeaders(exchange);
        String responseMessage = "Successfully shutdown.";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    /**
     * Sends our HTTP response back to the client.
     *
     * @param responseBytes Response body data.
     * @param exchange HttpExchange object
     */
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        if (responseBytes.length == 0) return;
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Retrieves state-representing values from the game manager and adds them
     * to the HTTP Exchange response headers.
     */
    private void addGameStateToHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().put("X-Player-Turn",
                Collections.singletonList(gameManager.getPlayerTurn()));
        exchange.getResponseHeaders().put("X-Waiting",
                Collections.singletonList(String.valueOf(!gameIsFull())));
        exchange.getResponseHeaders().put("X-Winner",
                Collections.singletonList(gameManager.getWinner()));
    }

    /**
     * Get bytes array from request body and return data as String.
     *
     * @param exchange HttpExchange Object.
     * @return String Request Body data.
     */
    private String getStringFromRequestBody(HttpExchange exchange) throws IOException {
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        return new String(requestBytes);
    }

    private boolean gameIsFull() {
        return (gameManager.numberOfPlayers() == 2);
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void shutdown() {
        server.stop(0);
    }
}
