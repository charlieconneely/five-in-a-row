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
    private static final String DEFAULT_ADDRESS = "http://localhost:8081";
    private static final String JOIN_ENDPOINT = "/join";
    private static final String STATE_CHECK_ENDPOINT = "/state";
    private static final String MOVE_ENDPOINT = "/move";

    private static final String PLAYER_TURN_HEADER = "X-Player-Turn";
    private static final String WAITING_HEADER = "X-Waiting";

    private boolean isOurTurn = false;
    private boolean waitingForOpponent = true;
    private boolean deciding = false;
    private String serverAddress;
    private String matrixAsText = "";

    private WebClient client;
    private Player player;

    public GameRunner() {
        this.client = new WebClient();
        this.player = new Player(null);
        this.serverAddress = DEFAULT_ADDRESS;
    }

    public void joinGame() throws IOException {
        getPlayerNameAsInput();
        String joinResult = sendJoinRequest(this.serverAddress + JOIN_ENDPOINT, player.getName());
        if (joinResult.contains("full")) {
            System.out.println(joinResult);
            System.exit(0);
        }
        System.out.println(joinResult);
        checkGameState();
        if (waitingForOpponent) System.out.println("Waiting for opponent to join...\n");
        runGame();
    }

    private void runGame() throws IOException {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkGameState();
            if (!isOurTurn || waitingForOpponent || deciding) continue;
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

    private void makeNextMove() throws IOException {
        this.deciding = true;
        String chosenColumn = "-1";
        String message = String.format("It's your turn %s, please enter column (1-9): ", player.getName());
        while (Integer.parseInt(chosenColumn) > 9 ||
            Integer.parseInt(chosenColumn) < 1) {
            chosenColumn = getInput(message);
        }
        client.sendMove(this.serverAddress + MOVE_ENDPOINT, chosenColumn.getBytes());
        System.out.println("Waiting for opponent...");
        this.deciding = false;
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
            }
        });
    }

    /**
     * Retrieve the user's name through the command line.
     *
     * @throws IOException
     */
    private void getPlayerNameAsInput() throws IOException {
        String name = getInput("Enter your name:");
        player.setName(name);
        System.out.printf("Welcome %s!%n", name);
    }

    private String getInput(String inputMessage) throws IOException {
        System.out.println(inputMessage);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return reader.readLine();
    }

    public void setServerAddress(String address) {
        this.serverAddress = address;
    }
}
