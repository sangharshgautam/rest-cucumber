package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaType;
import com.example.camel.cucumber.schema.SchemaValidator;
import com.example.camel.cucumber.wiremock.WireMockManager;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseSteps {

    @Autowired
    private ScenarioContext context;

    @Autowired
    private SchemaValidator schemaValidator;

    @Autowired
    private WireMockManager wireMockManager;

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        int actual = context.getResponse().getStatusCode();
        assertEquals(expectedStatus, actual,
                "Expected status " + expectedStatus + " but got " + actual);
    }

    @Then("the response status is not {int}")
    public void responseStatusIsNot(int unexpectedStatus) {
        int actual = context.getResponse().getStatusCode();
        assertNotEquals(unexpectedStatus, actual,
                "Status should not be " + unexpectedStatus);
    }

    @Then("the response body matches the response schema")
    public void responseBodyMatchesResponseSchema() {
        String responseSchemaPath = context.getResponseSchemaPath();
        assertNotNull(responseSchemaPath, "Response schema not configured. Use 'Given the response schema is at...'");
        validateBodyAgainstSchema(context.getResponse(), responseSchemaPath, context.getResponseSchemaType());
    }

    @Then("the response body matches the request schema")
    public void responseBodyMatchesRequestSchema() {
        String requestSchemaPath = context.getRequestSchemaPath();
        assertNotNull(requestSchemaPath, "Request schema not configured. Use 'Given the request schema is at...'");
        validateBodyAgainstSchema(context.getResponse(), requestSchemaPath, context.getRequestSchemaType());
    }

    @Then("the response body validates against the schema at {string}")
    public void responseBodyValidatesAgainstSchema(String schemaPath) {
        var schemaType = SchemaValidator.detectSchemaType(schemaPath);
        validateBodyAgainstSchema(context.getResponse(), schemaPath, schemaType);
    }

    private void validateBodyAgainstSchema(Response response, String schemaPath, SchemaType schemaType) {
        String body = response.getBody().asString();
        SchemaValidator.ValidationResult result = schemaValidator.validate(body, schemaPath, schemaType);
        assertTrue(result.isValid(), "Schema validation failed: " + String.join("; ", result.errors()));
    }

    @Then("the response body is:")
    public void responseBodyIs(String expectedBody) {
        String actual = context.getResponse().getBody().asString();
        assertEquals(expectedBody.trim(), actual.trim(),
                "Response body does not match expected content");
    }

    @Then("the response body contains {string}")
    public void responseBodyContains(String expectedContent) {
        String actual = context.getResponse().getBody().asString();
        assertTrue(actual.contains(expectedContent),
                "Response body does not contain: " + expectedContent);
    }

    @Then("the response body matches JSON path {string} is {string}")
    public void responseBodyJsonPathEquals(String jsonPath, String expectedValue) {
        JsonPath jsonPathEvaluator = context.getResponse().jsonPath();
        Object actual = jsonPathEvaluator.get(jsonPath);
        assertNotNull(actual, "JSON path '" + jsonPath + "' not found in response");
        assertEquals(expectedValue, actual.toString(),
                "JSON path '" + jsonPath + "' mismatch");
    }

    @Then("the response body XML matches XPath {string} is {string}")
    public void responseBodyXPathEquals(String xpath, String expectedValue) {
        XmlPath xmlPath = context.getResponse().xmlPath();
        String actual = xmlPath.getString(xpath);
        assertNotNull(actual, "XPath '" + xpath + "' not found in response");
        assertEquals(expectedValue, actual,
                "XPath '" + xpath + "' mismatch");
    }

    @Then("the response header {string} should be {string}")
    public void responseHeaderShouldBe(String headerName, String expectedValue) {
        String actual = context.getResponse().getHeader(headerName);
        assertNotNull(actual, "Header '" + headerName + "' not present in response");
        assertEquals(expectedValue, actual,
                "Header '" + headerName + "' mismatch");
    }

    @Then("the response content type should be {string}")
    public void responseContentTypeShouldBe(String expectedContentType) {
        String actual = context.getResponse().getContentType();
        assertNotNull(actual, "Content-Type header not present in response");
        assertTrue(actual.contains(expectedContentType),
                "Expected Content-Type containing '" + expectedContentType + "' but got '" + actual + "'");
    }

    @Then("the response time is less than {int}ms")
    public void responseTimeLessThan(int maxMs) {
        long actual = context.getResponse().getTime();
        assertTrue(actual < maxMs,
                "Response time " + actual + "ms exceeded maximum " + maxMs + "ms");
    }

    @Then("the backend was called exactly {int} time\\(s)")
    public void backendCalledExactly(int times) {
        wireMockManager.verifyCalled(times);
    }

    @Then("the backend was called at least {int} time\\(s)")
    public void backendCalledAtLeast(int times) {
        wireMockManager.verifyAtLeast(times);
    }

    @Then("the backend was never called")
    public void backendNeverCalled() {
        wireMockManager.verifyNeverCalled();
    }

    @Then("the request body validates against the request schema")
    public void requestBodyValidatesAgainstRequestSchema() {
        String requestBody = context.getRequestBody();
        assertNotNull(requestBody, "No request body configured");
        String schemaPath = context.getRequestSchemaPath();
        assertNotNull(schemaPath, "Request schema not configured");
        SchemaValidator.ValidationResult result = schemaValidator.validate(
                requestBody, schemaPath, context.getRequestSchemaType());
        assertTrue(result.isValid(), "Request body schema validation failed: "
                + String.join("; ", result.errors()));
    }
}
