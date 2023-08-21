package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

public class WireMockExampleTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        // Configure stubs
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/resource"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Hello, WireMock!\"}")));
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testMockedEndpoint() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:" + wireMockServer.port() + "/api/resource";
        System.out.println(apiUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));
        assertEquals("{\"message\": \"Hello, WireMock!\"}", response.getBody());
        System.out.println(response);

        // Verify that the expected request was made to WireMock
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/api/resource")));
    }
}
