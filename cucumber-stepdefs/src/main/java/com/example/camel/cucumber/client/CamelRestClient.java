package com.example.camel.cucumber.client;

import com.example.camel.cucumber.config.CamelCucumberProperties;
import com.example.camel.cucumber.context.ScenarioContext;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class CamelRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(CamelRestClient.class);

    private static final Set<String> BODY_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final String baseUrl;

    private final RestAssuredConfig config;

    public CamelRestClient(CamelCucumberProperties properties) {
        this.baseUrl = properties.getCamelUrl();
        this.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", properties.getConnectionTimeoutMs())
                        .setParam("http.socket.timeout", properties.getSocketTimeoutMs()));
    }

    public Response send(String method, String path, ScenarioContext context) {
        String resolvedMethod = (method != null) ? method.toUpperCase() : "POST";
        if (context.getMethod() != null) {
            resolvedMethod = context.getMethod().toUpperCase();
        }

        String url = baseUrl + path;
        LOG.info("Sending {} request to: {}", resolvedMethod, url);

        RequestSpecification spec = RestAssured.given()
                .config(config)
                .baseUri(baseUrl)
                .relaxedHTTPSValidation()
                .log().all();

        attachHeaders(spec, context.getRequestHeaders());
        attachQueryParams(spec, context.getQueryParams());

        if (context.getRequestContentType() != null) {
            spec.contentType(context.getRequestContentType());
        }

        if (hasRequestBody(resolvedMethod) && context.getRequestBody() != null) {
            spec.body(context.getRequestBody());
        }

        Response response = spec.request(resolvedMethod, path)
                .then()
                .log().all()
                .extract()
                .response();

        context.setMethod(resolvedMethod);
        context.setResponse(response);
        return response;
    }

    private void attachHeaders(RequestSpecification spec, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            spec.headers(headers);
        }
    }

    private void attachQueryParams(RequestSpecification spec, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            spec.queryParams(params);
        }
    }

    private boolean hasRequestBody(String method) {
        return BODY_METHODS.contains(method.toUpperCase());
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
