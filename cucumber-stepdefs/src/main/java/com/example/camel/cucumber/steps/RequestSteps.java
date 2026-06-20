package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaType;
import com.example.camel.cucumber.schema.SchemaValidator;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
public class RequestSteps {

    @Autowired
    private ScenarioContext context;

    @Given("the request payload from file {string}")
    public void requestPayloadFromFile(String filePath) {
        String content = SchemaValidator.loadFileContent(filePath);
        context.setRequestBody(content);
        if (context.getRequestContentType() == null) {
            context.setRequestContentType(SchemaValidator.detectContentType(filePath));
        }
    }

    @Given("the request body:")
    public void requestBody(String body) {
        context.setRequestBody(body);
    }

    @Given("the request body is empty")
    public void requestBodyIsEmpty() {
        context.setRequestBody("");
    }

    @Given("the request query parameter {string} is {string}")
    public void requestQueryParam(String name, String value) {
        context.getQueryParams().put(name, value);
    }

    @Given("the request query parameters:")
    public void requestQueryParams(Map<String, String> params) {
        context.getQueryParams().putAll(params);
    }

    @Given("the request header {string} is {string}")
    public void requestHeader(String name, String value) {
        context.getRequestHeaders().put(name, value);
    }

    @Given("the request headers:")
    public void requestHeaders(Map<String, String> headers) {
        context.getRequestHeaders().putAll(headers);
    }

    @Given("the request content type is {string}")
    public void requestContentType(String contentType) {
        context.setRequestContentType(contentType);
    }

    @Given("the request schema is at {string}")
    public void requestSchemaAt(String path) {
        context.setRequestSchemaPath(path);
        context.setRequestSchemaType(SchemaValidator.detectSchemaType(path));
    }

    @Given("the response schema is at {string}")
    public void responseSchemaAt(String path) {
        context.setResponseSchemaPath(path);
        context.setResponseSchemaType(SchemaValidator.detectSchemaType(path));
    }

    @Given("the request JSON schema is at {string}")
    public void requestJsonSchemaAt(String path) {
        context.setRequestSchemaPath(path);
        context.setRequestSchemaType(SchemaType.JSON_SCHEMA);
    }

    @Given("the response JSON schema is at {string}")
    public void responseJsonSchemaAt(String path) {
        context.setResponseSchemaPath(path);
        context.setResponseSchemaType(SchemaType.JSON_SCHEMA);
    }

    @Given("the request XSD schema is at {string}")
    public void requestXsdSchemaAt(String path) {
        context.setRequestSchemaPath(path);
        context.setRequestSchemaType(SchemaType.XSD);
    }

    @Given("the response XSD schema is at {string}")
    public void responseXsdSchemaAt(String path) {
        context.setResponseSchemaPath(path);
        context.setResponseSchemaType(SchemaType.XSD);
    }
}
