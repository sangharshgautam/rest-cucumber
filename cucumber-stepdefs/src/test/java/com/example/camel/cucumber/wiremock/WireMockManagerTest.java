package com.example.camel.cucumber.wiremock;

import com.example.camel.cucumber.config.CamelCucumberProperties;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class WireMockManagerTest {

    private static final int TEST_PORT = 9099;

    private WireMockManager manager;

    @BeforeEach
    void setUp() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setWiremockPort(TEST_PORT);
        props.setWiremockHost("localhost");
        manager = new WireMockManager(props);
    }

    @AfterEach
    void tearDown() {
        manager.stop();
    }

    @Test
    void startAndStop() {
        manager.start();
        assertDoesNotThrow(() -> sendGet("http://localhost:" + TEST_PORT + "/any"));
        manager.stop();
    }

    @Test
    void createStubAndVerifyResponse() throws Exception {
        manager.start();
        manager.createStub(200, "{\"status\":\"ok\"}", "application/json");

        HttpResponse<String> response = sendGet("http://localhost:" + TEST_PORT + "/test");
        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"ok\"}", response.body());
    }

    @Test
    void createStubWithStatusCode() throws Exception {
        manager.start();
        manager.createStub(404, "Not Found", "text/plain");

        HttpResponse<String> response = sendGet("http://localhost:" + TEST_PORT + "/missing");
        assertEquals(404, response.statusCode());
    }

    @Test
    void verifyCalledExactly() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");

        sendGet("http://localhost:" + TEST_PORT + "/a");
        sendGet("http://localhost:" + TEST_PORT + "/b");

        manager.verifyCalled(2);
    }

    @Test
    void verifyCalledExactlyFailsOnMismatch() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");
        sendGet("http://localhost:" + TEST_PORT + "/x");

        assertThrows(AssertionError.class, () -> manager.verifyCalled(2));
    }

    @Test
    void verifyAtLeast() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");

        sendGet("http://localhost:" + TEST_PORT + "/a");
        sendGet("http://localhost:" + TEST_PORT + "/b");
        sendGet("http://localhost:" + TEST_PORT + "/c");

        manager.verifyAtLeast(2);
    }

    @Test
    void verifyAtLeastFailsOnLowCount() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");
        sendGet("http://localhost:" + TEST_PORT + "/x");

        assertThrows(AssertionError.class, () -> manager.verifyAtLeast(3));
    }

    @Test
    void verifyNeverCalled() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");

        manager.verifyNeverCalled();
    }

    @Test
    void verifyNeverCalledFailsIfCalled() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");
        sendGet("http://localhost:" + TEST_PORT + "/x");

        assertThrows(AssertionError.class, () -> manager.verifyNeverCalled());
    }

    @Test
    void resetStubsClearsCount() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain");

        sendGet("http://localhost:" + TEST_PORT + "/x");
        manager.resetStubs();
        manager.verifyNeverCalled();
    }

    @Test
    void createStubWithHeaders() throws Exception {
        manager.start();
        Map<String, String> headers = Map.of("X-Custom", "test-value");
        manager.createStub(200, "ok", "text/plain", headers, null);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/headers"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("test-value", response.headers().firstValue("X-Custom").orElse(null));
    }

    @Test
    void createStubWithFixedDelay() throws Exception {
        manager.start();
        manager.createStub(200, "ok", "text/plain", null, 500);

        long start = System.currentTimeMillis();
        sendGet("http://localhost:" + TEST_PORT + "/delay");
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed >= 400, "Expected delay of at least 400ms, got " + elapsed + "ms");
    }

    @Test
    void defaultContentTypeIsJson() throws Exception {
        manager.start();
        manager.createStub(200, "{}");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/ct"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String ct = response.headers().firstValue("Content-Type").orElse("");
        assertTrue(ct.contains("application/json"));
    }

    @Test
    void customContentType() throws Exception {
        manager.start();
        manager.createStub(200, "<ok/>", "application/xml");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/xml"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String ct = response.headers().firstValue("Content-Type").orElse("");
        assertTrue(ct.contains("application/xml"));
    }

    @Test
    void emptyBody() throws Exception {
        manager.start();
        manager.createStub(204, "", "text/plain");

        HttpResponse<String> response = sendGet("http://localhost:" + TEST_PORT + "/empty");
        assertEquals(204, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    void nullBodyBecomesEmpty() throws Exception {
        manager.start();
        manager.createStub(200, null);

        HttpResponse<String> response = sendGet("http://localhost:" + TEST_PORT + "/nullbody");
        assertEquals("", response.body());
    }

    private HttpResponse<String> sendGet(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
