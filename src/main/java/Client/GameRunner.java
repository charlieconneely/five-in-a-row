package Client;

import Client.networking.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameRunner {
    private static GameRunner gameRunner = new GameRunner();

    private static final String DEFAULT_ADDRESS = "http://localhost:8081";
    private static final String JOIN_ENDPOINT = "/join";
    private static final String STATE_CHECK_ENDPOINT = "/state";
    private static final String MOVE_ENDPOINT = "/move";

    private static final String PLAYER_TURN_HEADER = "X-Player-Turn";
    private static final String WAITING_HEADER = "X-Waiting";
    private static final String WINNER_HEADER = "X-Winner";

    private boolean isOurTurn = false;
    private boolean waitingForOpponent = true;
    private boolean deciding = false;
    private boolean isGameFull = false;
    private boolean displayedWaitingMessage = false;
    private boolean winnerAnnounced = false;
    private String serverAddress;
    private String matrixAsText = "";

    private WebClient client;
    private Player player;

    private GameRunner() {
        this.client = new WebClient();
        this.player = new Player(null);
        this.serverAddress = DEFAULT_ADDRESS;
    }

    public void joinGame() throws IOException {
        getPlayerNameAsInput();
        String joinResult = sendJoinRequest(this.serverAddress + JOIN_ENDPOINT, player.getName());
        if (joinResult.contains("full")) {
            this.isGameFull = true;
            System.out.println(joinResult);
            System.exit(0);
        }
        System.out.println(joinResult);
        checkGameState();
        runGame();
    }

    private void runGame() throws IOException {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conditionalWaitingMessage();
            if (!timeToMakeAMove()) continue;
            System.out.println(matrixAsText);
            makeNextMove();
        }
    }

    private String sendJoinRequest(String address, String task) {
        byte[] requestPayload = task.getBytes();
        CompletableFuture<String> future = client.sendTask(address, requestPayload);
        return future.join();
    }

    /**
     * Check game state by retrieving custom HTTP Response headers.
     * Retrieve updated String representing matrix state from HTTP Response body.
     */
    private void checkGameState() {
        CompletableFuture<HttpResponse<String>> response = client.sendGameStateCheck(this.serverAddress
                + STATE_CHECK_ENDPOINT);
        matrixAsText = response.join().body();
        HttpHeaders headers = response.join().headers();
        analyseHeaders(headers);
    }

    /**
     * Request column choice from the command line.
     * Send choice to server.
     */
    private void makeNextMove() throws IOException {
        deciding = true;
        String choice = requestColumnChoice();
        // recheck response headers
        checkGameState();
        // if opponent has not left (!waiting) - send move
        if (!waitingForOpponent) client.sendMove(this.serverAddress + MOVE_ENDPOINT, choice.getBytes());
        deciding = displayedWaitingMessage = false;
    }

    private String requestColumnChoice() throws IOException {
        String chosenColumn = "-1";
        String message = String.format("It's your turn %s, please enter column (1-9): ", player.getName());
        while (Integer.parseInt(chosenColumn) > 9 ||
                Integer.parseInt(chosenColumn) < 1) {
            chosenColumn = getInput(message);
        }
        return chosenColumn;
    }

    /**
     * Analyze custom HTTP headers and update state-representing variables accordingly.
     *
     * @param headers
     */
    private void analyseHeaders(HttpHeaders headers) {
        headers.map().forEach((k, v) -> {
            if (k.equalsIgnoreCase(PLAYER_TURN_HEADER)) {
                isOurTurn = (v.get(0).equalsIgnoreCase(player.getName()));
            } else if (k.equalsIgnoreCase(WAITING_HEADER))  {
                waitingForOpponent = (v.get(0).equalsIgnoreCase("true"));
            } else if (k.equalsIgnoreCase(WINNER_HEADER)) {
                if (v.get(0).equals("")) return;
                announceWinner(v.get(0));
            }
        });
    }

    private void announceWinner(String name) {
        String message;
        winnerAnnounced = true;
        if (name.equals(player.getName())) {
            message = "\nCongratulation!! You won the game!\n";
        } else {
            message = String.format("\nSorry - %s has won the game.\n", name);
        }
        System.out.println(message);
        System.exit(0);
    }

    /**
     * Retrieve the user's name through the command line.
     */
    private void getPlayerNameAsInput() throws IOException {
        String name = getInput("Enter your name:");
        player.setName(name);
        System.out.printf("Welcome %s!%n", name);
    }

    private String getInput(String promptMessage) throws IOException {
        System.out.println(promptMessage);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return reader.readLine();
    }

    /**
     * Checks response headers and displays wait message accordingly.
     */
    private void conditionalWaitingMessage() {
        checkGameState();
        if (waitingForOpponent && !displayedWaitingMessage) {
            System.out.println("Waiting for opponent to join...\n");
            displayedWaitingMessage = true;
        }
    }

    private boolean timeToMakeAMove() {
        return (isOurTurn && !waitingForOpponent && !deciding);
    }

    public String getPlayerName() { return player.getName(); }

    public boolean gameIsFull() { return this.isGameFull; }

    public boolean getIsWinnerAnnounced() { return this.winnerAnnounced; }

    public void setServerAddress(String address) { this.serverAddress = address; }

    public static GameRunner getInstance() {
        return gameRunner;
    }
}
