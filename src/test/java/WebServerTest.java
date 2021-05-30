import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import Server.GameManager;
import Server.networking.WebServer;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;

// Allows us to mock classes
@ExtendWith(MockitoExtension.class)
public class WebServerTest {

    private final String serverAddress = "http://localhost:8080";

    private WebServer webServer;
    @Mock private GameManager mockGameManager;

    @BeforeEach
    void setUp() {
        webServer = new WebServer(8080);
        webServer.setGameManager(mockGameManager);
        webServer.startServer();
    }

    @Test
    @DisplayName("/status endpoint returns status 200.")
    void testStatusRequest() throws IOException {
        HttpUriRequest request = new HttpGet(serverAddress + "/status");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("/state endpoint returns status 200.")
    void testStateRequest() throws IOException {
        stubGameManagerStateMethods();
        doReturn("fakeBoard").when(mockGameManager).getBoardStateAsText();

        HttpUriRequest request = new HttpGet(serverAddress + "/state");
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("/state adds game state variables to response headers.")
    void checkStateResponseHeaders() throws IOException {
        String turn = "your turn";
        stubGameManagerStateMethods();
        doReturn("fakeBoard").when(mockGameManager).getBoardStateAsText();

        HttpUriRequest request = new HttpGet(serverAddress + "/state");
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);

        // Retrieve our response headers.
        Header[] headers = response.getAllHeaders();
        boolean present = Arrays.asList(headers).toString().toLowerCase().contains(turn);
        assertTrue(present);
    }

    @Test
    @DisplayName("/move endpoint calls handlePlayerMove and returns status 200.")
    void testMoveRequest() throws IOException {
        String columnText = "4";
        int columnInt = Integer.parseInt(columnText) - 1;

        // Create Http request to /move
        HttpPost request = new HttpPost(serverAddress + "/move");
        // Assign column choice to request body
        request.setEntity(new StringEntity(columnText));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        verify(mockGameManager).handlePlayerMove(columnInt);
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("/join endpoint returns status 200")
    void testJoinEndpoint() throws IOException {
        doReturn(1).when(mockGameManager).numberOfPlayers();
        doReturn("players").when(mockGameManager).getPlayers();

        String name = "mockName";
        // Create Http request to /join
        HttpPost request = new HttpPost(serverAddress + "/join");
        // Assign name to request body
        request.setEntity(new StringEntity(name));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("/join endpoint returns sorry when game if full.")
    void testJoinRequestWhenGameIsFull() throws IOException {
        doReturn(2).when(mockGameManager).numberOfPlayers();
        String expectedResponseMessage = "Sorry, the game is full.";

        // Create Http request to /join
        HttpPost request = new HttpPost(serverAddress + "/join");
        // Assign name to request body
        request.setEntity(new StringEntity("name"));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String actualResponse = new String(httpResponse.getEntity().getContent().readAllBytes());

        assertEquals(expectedResponseMessage, actualResponse);
    }

    @Test
    @DisplayName("/join endpoint adds our name to a list when game is not full.")
    void testJoinRequestWhenGameIsNotFull() throws IOException {
        doReturn(1).when(mockGameManager).numberOfPlayers();

        // Create Http request to /join
        HttpPost request = new HttpPost(serverAddress + "/join");
        String mockName = "mocky";
        // Assign name to request body
        request.setEntity(new StringEntity(mockName));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // verify that local name is passed to addPlayer
        verify(mockGameManager).addPlayer(mockName);
    }

    @Test
    @DisplayName("/quit endpoint returns status 200")
    void testQuitEndpoint() throws IOException {
        stubGameManagerStateMethods();

        // Create Http request to /join
        HttpPost request = new HttpPost(serverAddress + "/quit");
        String mockName = "mocky";
        // Assign name to request body
        request.setEntity(new StringEntity(mockName));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("/quit endpoint removes our name from the players list.")
    void testIfQuitEndpointRemovesOurName() throws IOException {
        stubGameManagerStateMethods();

        // Create Http request to /join
        HttpPost request = new HttpPost(serverAddress + "/quit");
        String mockName = "mocky";
        // Assign name to request body
        request.setEntity(new StringEntity(mockName));
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        verify(mockGameManager).removePlayer(mockName);
    }

    void stubGameManagerStateMethods() {
        // Return random values from mock method invocations
        doReturn("your turn").when(mockGameManager).getPlayerTurn();
        doReturn(2).when(mockGameManager).numberOfPlayers();
        doReturn("fakeName").when(mockGameManager).getWinner();
    }

    @AfterEach
    void shutdown() {
        webServer.shutdown();
    }
}
