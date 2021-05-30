import Client.networking.WebClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebClientTest {

    private final int PORT = 8080;
    private final String URL = "http://localhost:" + PORT;

    private WebClient webClient;
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUpServer() {
        webClient = new WebClient();
        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
        wireMockServer.start();
    }

    @Test
    void sendStatusCheck() {
        String url = URL + "/status";
        String expectedResponse = "Server is alive!\n";

        // Create server mock
        wireMockServer.stubFor(get(urlEqualTo("/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expectedResponse)));

        String response = webClient.sendStatusCheck(url).join();
        assertEquals(expectedResponse, response);
    }

    @AfterEach
    void shutdown() {
        wireMockServer.shutdown();
    }
}
