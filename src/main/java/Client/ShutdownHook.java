package Client;

import Client.networking.WebClient;

public class ShutdownHook extends Thread {

    private static final String SHUTDOWN_ENDPOINT = "/quit";

    private GameRunner gameRunner;
    private WebClient client;
    private String serverAddress;

    public ShutdownHook(WebClient client, String address) {
        this.gameRunner = GameRunner.getInstance();
        this.client = client;
        this.serverAddress = address + SHUTDOWN_ENDPOINT;
    }

    public void run() {
        if (clientDidNotJoinGame() || winnerAnnounced()) return;
        System.out.println("Running shutdown hook...");
        client.sendShutDownRequest(serverAddress, gameRunner.getPlayerName().getBytes());
    }

    private boolean winnerAnnounced() {
        return (gameRunner.getIsWinnerAnnounced());
    }

    private boolean clientDidNotJoinGame() {
        return (gameRunner.gameIsFull() || gameRunner.getPlayerName() == null);
    }

}
