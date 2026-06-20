package com.example.camel.cucumber.client;

import com.example.camel.cucumber.config.CamelCucumberProperties;
import com.example.camel.cucumber.context.ScenarioContext;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

class CamelRestClientTest {

    private static final int WIREMOCK_PORT = 9098;

    private WireMockServer wireMockServer;
    private CamelRestClient client;
    private ScenarioContext context;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT));
        wireMockServer.start();

        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setCamelUrl("http://localhost:" + WIREMOCK_PORT);
        client = new CamelRestClient(props);
        context = new ScenarioContext();
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Test
    void sendGetRequest() {
        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse().withStatus(200).withBody("get ok")));

        Response response = client.send("GET", "/test", context);

        assertEquals(200, response.getStatusCode());
        assertEquals("get ok", response.getBody().asString());
        assertEquals("GET", context.getMethod());
    }

    @Test
    void sendPostRequestWithBody() {
        wireMockServer.stubFor(post(anyUrl())
                .willReturn(aResponse().withStatus(201).withBody("created")));

        context.setRequestBody("{\"key\":\"value\"}");
        context.setRequestContentType("application/json");

        Response response = client.send("POST", "/resource", context);

        assertEquals(201, response.getStatusCode());
        assertEquals("created", response.getBody().asString());

        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/resource"))
                .withRequestBody(equalToJson("{\"key\":\"value\"}")));
    }

    @Test
    void sendPutRequestWithBody() {
        wireMockServer.stubFor(put(anyUrl())
                .willReturn(aResponse().withStatus(200).withBody("updated")));

        context.setRequestBody("{\"key\":\"new\"}");
        context.setRequestContentType("application/json");

        Response response = client.send("PUT", "/resource/1", context);

        assertEquals(200, response.getStatusCode());
        wireMockServer.verify(1, putRequestedFor(urlEqualTo("/resource/1")));
    }

    @Test
    void sendDeleteRequestWithBody() {
        wireMockServer.stubFor(delete(anyUrl())
                .willReturn(aResponse().withStatus(204)));

        Response response = client.send("DELETE", "/resource/1", context);

        assertEquals(204, response.getStatusCode());
        wireMockServer.verify(1, deleteRequestedFor(urlEqualTo("/resource/1")));
    }

    @Test
    void sendPatchRequestWithBody() {
        wireMockServer.stubFor(patch(anyUrl())
                .willReturn(aResponse().withStatus(200).withBody("patched")));

        context.setRequestBody("{\"op\":\"replace\"}");

        Response response = client.send("PATCH", "/resource/1", context);

        assertEquals(200, response.getStatusCode());
        wireMockServer.verify(1, patchRequestedFor(urlEqualTo("/resource/1")));
    }

    @Test
    void sendHeadRequest() {
        wireMockServer.stubFor(head(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        Response response = client.send("HEAD", "/resource", context);

        assertEquals(200, response.getStatusCode());
        wireMockServer.verify(1, headRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    void sendOptionsRequest() {
        wireMockServer.stubFor(options(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        Response response = client.send("OPTIONS", "/resource", context);

        assertEquals(200, response.getStatusCode());
        wireMockServer.verify(1, optionsRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    void defaultMethodIsPostWhenNull() {
        wireMockServer.stubFor(post(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        Response response = client.send(null, "/default", context);

        assertEquals(200, response.getStatusCode());
        assertEquals("POST", context.getMethod());
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/default")));
    }

    @Test
    void contextMethodOverridesParameter() {
        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        context.setMethod("GET");
        Response response = client.send("POST", "/override", context);

        assertEquals(200, response.getStatusCode());
        assertEquals("GET", context.getMethod());
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/override")));
    }

    @Test
    void attachRequestHeaders() {
        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        context.getRequestHeaders().put("Authorization", "Bearer test-token");
        context.getRequestHeaders().put("X-Custom", "value");

        client.send("GET", "/headers", context);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/headers"))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .withHeader("X-Custom", equalTo("value")));
    }

    @Test
    void attachQueryParams() {
        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        context.getQueryParams().put("id", "123");
        context.getQueryParams().put("type", "test");

        client.send("GET", "/query", context);

        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/query"))
                .withQueryParam("id", equalTo("123"))
                .withQueryParam("type", equalTo("test")));
    }

    @Test
    void noBodyForGetRequest() {
        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse().withStatus(200)));

        context.setRequestBody("should not be sent");
        client.send("GET", "/nobody", context);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/nobody")));
    }

    @Test
    void getBaseUrl() {
        assertEquals("http://localhost:" + WIREMOCK_PORT, client.getBaseUrl());
    }
}
