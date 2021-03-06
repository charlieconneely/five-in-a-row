package Client;

import Client.networking.WebClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Application {

    private static final String SERVER_ADDRESS = "http://localhost:";
    private static final int DEFAULT_PORT = 8082;
    private static final String STATUS_ENDPOINT = "/status";

    WebClient client;
    GameRunner gameRunner;

    public Application() {
        this.client = new WebClient();
        this.gameRunner = GameRunner.getInstance();
    }

    public static void main(String[] args) throws IOException {
        Application application = new Application();

        String serverAddress = SERVER_ADDRESS + DEFAULT_PORT;
        // Port as optional cli argument
        if (args.length == 1) serverAddress = SERVER_ADDRESS + args[0];

        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new ShutdownHook(application.client, serverAddress));

        // Perform status check
        String status = application.sendStatusCheck(serverAddress + STATUS_ENDPOINT);
        System.out.println(status);

        application.startGame(serverAddress);
    }

    private void startGame(String address) throws IOException {
        gameRunner.setServerAddress(address);
        gameRunner.joinGame();
    }

    public String sendStatusCheck(String address) throws IOException {
        CompletableFuture<String> future = client.sendStatusCheck(address);
        return future.join();
    }
}
