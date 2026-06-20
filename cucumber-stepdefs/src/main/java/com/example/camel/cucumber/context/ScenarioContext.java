package com.example.camel.cucumber.context;

import com.example.camel.cucumber.schema.SchemaType;
import io.restassured.response.Response;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScenarioContext {

    private String method;

    private String requestBody;

    private String requestContentType;

    private final Map<String, String> requestHeaders = new HashMap<>();

    private final Map<String, String> queryParams = new HashMap<>();

    private String requestSchemaPath;

    private String responseSchemaPath;

    private SchemaType requestSchemaType;

    private SchemaType responseSchemaType;

    private Response response;

    private Integer backendDelayMs;

    private Map<String, String> backendResponseHeaders;

    private String backendResponseContentType;

    public void reset() {
        this.method = null;
        this.requestBody = null;
        this.requestContentType = null;
        this.requestHeaders.clear();
        this.queryParams.clear();
        this.requestSchemaPath = null;
        this.responseSchemaPath = null;
        this.requestSchemaType = null;
        this.responseSchemaType = null;
        this.response = null;
        this.backendDelayMs = null;
        this.backendResponseHeaders = null;
        this.backendResponseContentType = null;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getRequestSchemaPath() {
        return requestSchemaPath;
    }

    public void setRequestSchemaPath(String requestSchemaPath) {
        this.requestSchemaPath = requestSchemaPath;
    }

    public String getResponseSchemaPath() {
        return responseSchemaPath;
    }

    public void setResponseSchemaPath(String responseSchemaPath) {
        this.responseSchemaPath = responseSchemaPath;
    }

    public SchemaType getRequestSchemaType() {
        return requestSchemaType;
    }

    public void setRequestSchemaType(SchemaType requestSchemaType) {
        this.requestSchemaType = requestSchemaType;
    }

    public SchemaType getResponseSchemaType() {
        return responseSchemaType;
    }

    public void setResponseSchemaType(SchemaType responseSchemaType) {
        this.responseSchemaType = responseSchemaType;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Integer getBackendDelayMs() {
        return backendDelayMs;
    }

    public void setBackendDelayMs(Integer backendDelayMs) {
        this.backendDelayMs = backendDelayMs;
    }

    public Map<String, String> getBackendResponseHeaders() {
        return backendResponseHeaders;
    }

    public void setBackendResponseHeaders(Map<String, String> backendResponseHeaders) {
        this.backendResponseHeaders = backendResponseHeaders;
    }

    public String getBackendResponseContentType() {
        return backendResponseContentType;
    }

    public void setBackendResponseContentType(String backendResponseContentType) {
        this.backendResponseContentType = backendResponseContentType;
    }
}
