package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.client.CamelRestClient;
import com.example.camel.cucumber.context.ScenarioContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
public class RestActionSteps {

    @Autowired
    private CamelRestClient camelRestClient;

    @Autowired
    private ScenarioContext context;

    @When("I send a {word} request to {string}")
    public void sendRequest(String method, String path) {
        Response response = camelRestClient.send(method, path, context);
        context.setResponse(response);
    }

    @When("I send a {word} request to {string} with query parameters:")
    public void sendRequestWithQueryParams(String method, String path, Map<String, String> queryParams) {
        context.getQueryParams().putAll(queryParams);
        Response response = camelRestClient.send(method, path, context);
        context.setResponse(response);
    }

    @When("I send the request to {string}")
    public void sendPreparedRequest(String path) {
        Response response = camelRestClient.send(null, path, context);
        context.setResponse(response);
    }
}
