package Client;

import Client.networking.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class GameRunner {
    private static final String DEFAULT_ADDRESS = "http://localhost:8081";
    private static final String JOIN_ENDPOINT = "/join";

    private String serverAddress;
    Player player;
    WebClient client;

    public GameRunner() {
        this.client = new WebClient();
        this.player = new Player(null);
        this.serverAddress = DEFAULT_ADDRESS;
    }

    public void startGame() throws IOException {
        getPlayerName();
        String result = sendJoinRequest(this.serverAddress + JOIN_ENDPOINT, player.getName());
        System.out.println(result);
    }

    private String sendJoinRequest(String address, String task) {
        byte[] requestPayload = task.getBytes();
        CompletableFuture<String> future = client.sendTask(address, requestPayload);
        return future.join();
    }

    private String sendGameStateCheck(String address) {

        return null;
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
