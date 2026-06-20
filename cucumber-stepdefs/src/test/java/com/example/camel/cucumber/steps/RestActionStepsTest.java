package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.wiremock.WireMockManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StepdefsTestConfig.class)
class RestActionStepsTest {

    @Autowired
    private RestActionSteps steps;

    @Autowired
    private ScenarioContext context;

    @Autowired
    private WireMockManager wireMockManager;

    @BeforeEach
    void setUp() {
        wireMockManager.start();
        wireMockManager.resetStubs();
        context.reset();
    }

    @AfterEach
    void tearDown() {
        wireMockManager.stop();
    }

    @Test
    void sendGetRequestStoresResponse() {
        wireMockManager.createStub(200, "get ok", "text/plain");

        steps.sendRequest("GET", "/test");

        assertNotNull(context.getResponse());
        assertEquals(200, context.getResponse().getStatusCode());
        assertEquals("GET", context.getMethod());
    }

    @Test
    void sendPostRequestWithBody() {
        wireMockManager.createStub(201, "created", "application/json");
        context.setRequestBody("{\"key\":\"value\"}");
        context.setRequestContentType("application/json");

        steps.sendRequest("POST", "/resource");

        assertNotNull(context.getResponse());
        assertEquals(201, context.getResponse().getStatusCode());
    }

    @Test
    void sendRequestWithQueryParams() {
        wireMockManager.createStub(200, "ok", "text/plain");
        Map<String, String> params = Map.of("id", "42", "sort", "asc");

        steps.sendRequestWithQueryParams("GET", "/search", params);

        assertNotNull(context.getResponse());
        assertEquals(200, context.getResponse().getStatusCode());
        assertEquals("42", context.getQueryParams().get("id"));
    }

    @Test
    void sendPreparedRequestUsesContextMethod() {
        wireMockManager.createStub(200, "ok", "text/plain");
        context.setMethod("DELETE");

        steps.sendPreparedRequest("/resource/1");

        assertNotNull(context.getResponse());
        assertEquals("DELETE", context.getMethod());
    }

    @Test
    void sendPreparedRequestDefaultsToPost() {
        wireMockManager.createStub(200, "ok", "text/plain");

        steps.sendPreparedRequest("/default");

        assertNotNull(context.getResponse());
        assertEquals("POST", context.getMethod());
    }

    @Test
    void getRequest() {
        wireMockManager.createStub(200, "get ok", "text/plain");
        steps.sendRequest("GET", "/data");
        assertEquals(200, context.getResponse().getStatusCode());
    }

    @Test
    void postRequest() {
        wireMockManager.createStub(200, "post ok", "text/plain");
        context.setRequestBody("body");
        steps.sendRequest("POST", "/data");
        assertEquals(200, context.getResponse().getStatusCode());
    }

    @Test
    void putRequest() {
        wireMockManager.createStub(200, "put ok", "text/plain");
        context.setRequestBody("body");
        steps.sendRequest("PUT", "/data/1");
        assertEquals(200, context.getResponse().getStatusCode());
    }

    @Test
    void deleteRequest() {
        wireMockManager.createStub(200, "delete ok", "text/plain");
        context.setRequestBody("body");
        steps.sendRequest("DELETE", "/data/1");
        assertEquals(200, context.getResponse().getStatusCode());
    }

    @Test
    void patchRequest() {
        wireMockManager.createStub(200, "patch ok", "text/plain");
        context.setRequestBody("body");
        steps.sendRequest("PATCH", "/data/1");
        assertEquals(200, context.getResponse().getStatusCode());
    }
}
