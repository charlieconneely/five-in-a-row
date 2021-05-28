package Client;

import Client.networking.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameRunner {
    private static final String DEFAULT_ADDRESS = "http://localhost:8081";
    private static final String JOIN_ENDPOINT = "/join";
    private static final String STATE_CHECK_ENDPOINT = "/state";

    private boolean isOurTurn = false;
    private boolean waitingForOpponent = true;
    private String serverAddress;
    Player player;
    WebClient client;

    public GameRunner() {
        this.client = new WebClient();
        this.player = new Player(null);
        this.serverAddress = DEFAULT_ADDRESS;
    }

    public void joinGame() throws IOException {
        getPlayerName();
        String joinResult = sendJoinRequest(this.serverAddress + JOIN_ENDPOINT, player.getName());
        if (joinResult.contains("full")) {
            System.out.println(joinResult);
            System.exit(0);
        }
        System.out.println(joinResult);
        checkGameState();
        if (waitingForOpponent) System.out.println("Waiting for opponent...\n");
        runGame();
    }

    private void runGame()  {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkGameState();
            if (!isOurTurn || waitingForOpponent) continue;
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
     */
    private void checkGameState() {
        HttpHeaders headers = client.sendGameStateCheck(this.serverAddress + STATE_CHECK_ENDPOINT);
        analyseHeaders(headers);
    }

    private void makeNextMove() {

    }

    /**
     * Analyze custom HTTP headers and update state-representing variables accordingly.
     *
     * @param headers
     */
    private void analyseHeaders(HttpHeaders headers) {
        headers.map().forEach((k, v) -> {
            if (k.equalsIgnoreCase("X-Player-Turn")) {
                isOurTurn = (v.get(0).equalsIgnoreCase(player.getName()));
            } else if (k.equalsIgnoreCase("X-Waiting"))  {
                waitingForOpponent = (v.get(0).equalsIgnoreCase("true"));
            }
        });
    }

    /**
     * Retrieve the user's name through the command line.
     *
     * @throws IOException
     */
    private void getPlayerName() throws IOException {
        System.out.println("Enter your name:");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        String name = reader.readLine();
        player.setName(name);
        System.out.println(String.format("Welcome %s!", name));
    }

    public void setServerAddress(String address) {
        this.serverAddress = address;
    }
}
