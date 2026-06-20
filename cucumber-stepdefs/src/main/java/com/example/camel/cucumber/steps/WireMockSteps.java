package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaValidator;
import com.example.camel.cucumber.wiremock.WireMockManager;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
public class WireMockSteps {

    @Autowired
    private WireMockManager wireMockManager;

    @Autowired
    private ScenarioContext context;

    @Given("the backend will respond with status {int} and body from file {string}")
    public void backendRespondsWithStatusAndBodyFromFile(int status, String filePath) {
        String body = SchemaValidator.loadFileContent(filePath);
        String contentType = SchemaValidator.detectContentType(filePath);
        wireMockManager.createStub(status, body, contentType,
                context.getBackendResponseHeaders(), context.getBackendDelayMs());
    }

    @Given("the backend will respond with status {int} and body:")
    public void backendRespondsWithStatusAndBody(int status, String body) {
        wireMockManager.createStub(status, body, "application/json",
                context.getBackendResponseHeaders(), context.getBackendDelayMs());
    }

    @Given("the backend will respond with status {int} and empty body")
    public void backendRespondsWithStatusAndEmptyBody(int status) {
        wireMockManager.createStub(status, "", null,
                context.getBackendResponseHeaders(), context.getBackendDelayMs());
    }

    @Given("the backend will respond with status {int}")
    public void backendRespondsWithStatus(int status) {
        wireMockManager.createStub(status, "", null,
                context.getBackendResponseHeaders(), context.getBackendDelayMs());
    }

    @Given("the backend will respond after a delay of {int}ms")
    public void backendRespondsAfterDelay(int delayMs) {
        context.setBackendDelayMs(delayMs);
    }

    @Given("the backend will respond with headers:")
    public void backendRespondsWithHeaders(Map<String, String> headers) {
        context.setBackendResponseHeaders(headers);
    }

    @Given("the backend will respond with content type {string}")
    public void backendRespondsWithContentType(String contentType) {
        context.setBackendResponseContentType(contentType);
    }
}
