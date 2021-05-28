package Client;

import Client.networking.WebClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Application {

    private static final String SERVER_ADDRESS = "http://localhost:";
    private static final int DEFAULT_PORT = 8081;
    private static final String STATUS_ENDPOINT = "/status";
    private static final String TASK_ENDPOINT = "/task";

    WebClient client;
    GameRunner gameRunner;

    public Application() {
        this.client = new WebClient();
        this.gameRunner = new GameRunner();
    }

    public static void main(String[] args) throws IOException {
        Application application = new Application();
        String serverAddress = SERVER_ADDRESS + DEFAULT_PORT;
        // Port as optional cli argument
        if (args.length == 1) serverAddress = SERVER_ADDRESS + args[0];

        // Perform status check
        String status = null;

        status = application.sendStatusCheck(serverAddress + STATUS_ENDPOINT);
        System.out.println(status);

        application.startGame(serverAddress);
    }

    private void startGame(String address) throws IOException {
        gameRunner.setServerAddress(address);
        gameRunner.joinGame();
    }

    public String sendTasks(String address, String task) {
        byte[] requestPayload = task.getBytes();
        CompletableFuture<String> future = client.sendTask(address, requestPayload);
        return future.join();
    }

    public String sendStatusCheck(String address) throws IOException {
        CompletableFuture<String> future = client.sendStatusCheck(address);
        return future.join();
    }
}
