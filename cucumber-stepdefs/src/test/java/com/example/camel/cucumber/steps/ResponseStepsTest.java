package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaType;
import com.example.camel.cucumber.wiremock.WireMockManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StepdefsTestConfig.class)
class ResponseStepsTest {

    @Autowired
    private ResponseSteps responseSteps;

    @Autowired
    private RestActionSteps restActionSteps;

    @Autowired
    private WireMockSteps wireMockSteps;

    @Autowired
    private RequestSteps requestSteps;

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

    private void executeGet(String path) {
        wireMockManager.createStub(200, "ok", "text/plain");
        restActionSteps.sendRequest("GET", path);
    }

    @Test
    void responseStatusShouldBe() {
        executeGet("/status");
        responseSteps.responseStatusShouldBe(200);
    }

    @Test
    void responseStatusIsNot() {
        wireMockManager.createStub(404, "not found", "text/plain");
        restActionSteps.sendRequest("GET", "/notfound");
        responseSteps.responseStatusIsNot(200);
    }

    @Test
    void responseBodyIs() {
        wireMockManager.createStub(200, "expected body", "text/plain");
        restActionSteps.sendRequest("GET", "/body");
        responseSteps.responseBodyIs("expected body");
    }

    @Test
    void responseBodyContains() {
        wireMockManager.createStub(200, "hello world from server", "text/plain");
        restActionSteps.sendRequest("GET", "/contains");
        responseSteps.responseBodyContains("world");
    }

    @Test
    void responseHeaderShouldBe() {
        wireMockManager.createStub(200, "ok", "text/plain", java.util.Map.of("X-Custom", "header-value"), null);
        restActionSteps.sendRequest("GET", "/header");
        responseSteps.responseHeaderShouldBe("X-Custom", "header-value");
    }

    @Test
    void responseContentTypeShouldBe() {
        wireMockManager.createStub(200, "ok", "application/xml");
        restActionSteps.sendRequest("GET", "/ct");
        responseSteps.responseContentTypeShouldBe("application/xml");
    }

    @Test
    void responseTimeIsLessThan() {
        executeGet("/time");
        responseSteps.responseTimeLessThan(10000);
    }

    @Test
    void backendWasCalledExactly() {
        executeGet("/exactly");
        responseSteps.backendCalledExactly(1);
    }

    @Test
    void backendWasNeverCalled() {
        responseSteps.backendNeverCalled();
    }

    @Test
    void backendWasCalledAtLeast() {
        wireMockManager.createStub(200, "ok", "text/plain");
        restActionSteps.sendRequest("GET", "/a");
        restActionSteps.sendRequest("GET", "/b");
        responseSteps.backendCalledAtLeast(2);
    }

    @Test
    void responseBodyMatchesJsonSchema() throws Exception {
        wireMockManager.createStub(200, "{\"name\":\"John\",\"age\":30}", "application/json");
        context.setResponseSchemaPath("test-schema.json");
        context.setResponseSchemaType(SchemaType.JSON_SCHEMA);
        restActionSteps.sendRequest("GET", "/json-valid");
        responseSteps.responseBodyMatchesResponseSchema();
    }

    @Test
    void responseBodyValidatesAgainstSchemaPath() throws Exception {
        wireMockManager.createStub(200, "{\"name\":\"Jane\",\"age\":25}", "application/json");
        restActionSteps.sendRequest("GET", "/json-schema");
        responseSteps.responseBodyValidatesAgainstSchema("test-schema.json");
    }

    @Test
    void requestBodyValidatesAgainstRequestSchema() throws Exception {
        requestSteps.requestSchemaAt("test-schema.json");
        requestSteps.requestPayloadFromFile("valid-payload.json");
        wireMockManager.createStub(200, "ok", "text/plain");
        restActionSteps.sendRequest("POST", "/validate-request");
        responseSteps.requestBodyValidatesAgainstRequestSchema();
    }

    @Test
    void responseBodyMatchesRequestSchema() throws Exception {
        requestSteps.requestSchemaAt("test-schema.json");
        wireMockManager.createStub(200, "{\"name\":\"Bob\",\"age\":40}", "application/json");
        restActionSteps.sendRequest("GET", "/match-request-schema");
        responseSteps.responseBodyMatchesRequestSchema();
    }

    @Test
    void responseBodyJsonPathEquals() {
        wireMockManager.createStub(200, "{\"user\":{\"name\":\"John\"}}", "application/json");
        restActionSteps.sendRequest("GET", "/jsonpath");
        responseSteps.responseBodyJsonPathEquals("user.name", "John");
    }

    @Test
    void responseBodyXPathEquals() {
        wireMockManager.createStub(200, "<?xml version=\"1.0\"?><root><item id=\"1\">value</item></root>",
                "application/xml");
        restActionSteps.sendRequest("GET", "/xpath");
        responseSteps.responseBodyXPathEquals("root.item", "value");
    }
}
