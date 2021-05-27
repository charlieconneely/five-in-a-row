package Client;

import Client.networking.WebClient;

import java.util.concurrent.CompletableFuture;

public class Application {

    private static final String SERVER_ADDRESS = "http://localhost:";
    private static final int DEFAULT_PORT = 8081;
    private static final String STATUS_ENDPOINT = "/status";
    private static final String TASK_ENDPOINT = "/task";

    WebClient client;

    public Application() {
        this.client = new WebClient();
    }

    public static void main(String[] args) {
        Application application = new Application();
        String serverAddress = SERVER_ADDRESS + DEFAULT_PORT;
        if (args.length == 1) serverAddress = SERVER_ADDRESS + args[0];

        // Perform status check
        System.out.println(application.sendStatusCheck(serverAddress + STATUS_ENDPOINT));

        String task = "10,355";
        String result = application.sendTasks(serverAddress + TASK_ENDPOINT, task);
        System.out.println(result);
    }

    public String sendTasks(String address, String task) {
        byte[] requestPayload = task.getBytes();
        CompletableFuture<String> future = client.sendTask(address, requestPayload);
        return future.join();
    }

    public String sendStatusCheck(String address) {
        CompletableFuture<String> future = client.sendStatusCheck(address);
        return future.join();
    }
}
