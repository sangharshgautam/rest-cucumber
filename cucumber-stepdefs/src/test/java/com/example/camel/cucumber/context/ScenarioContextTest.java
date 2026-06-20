package com.example.camel.cucumber.context;

import com.example.camel.cucumber.schema.SchemaType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ScenarioContextTest {

    private ScenarioContext context;

    @BeforeEach
    void setUp() {
        context = new ScenarioContext();
    }

    @Test
    void initialState() {
        assertNull(context.getMethod());
        assertNull(context.getRequestBody());
        assertNull(context.getRequestContentType());
        assertTrue(context.getRequestHeaders().isEmpty());
        assertTrue(context.getQueryParams().isEmpty());
        assertNull(context.getRequestSchemaPath());
        assertNull(context.getResponseSchemaPath());
        assertNull(context.getRequestSchemaType());
        assertNull(context.getResponseSchemaType());
        assertNull(context.getResponse());
        assertNull(context.getBackendDelayMs());
        assertNull(context.getBackendResponseHeaders());
        assertNull(context.getBackendResponseContentType());
    }

    @Test
    void setAndGetMethod() {
        context.setMethod("POST");
        assertEquals("POST", context.getMethod());
    }

    @Test
    void setAndGetRequestBody() {
        context.setRequestBody("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", context.getRequestBody());
    }

    @Test
    void setAndGetRequestContentType() {
        context.setRequestContentType("application/json");
        assertEquals("application/json", context.getRequestContentType());
    }

    @Test
    void requestHeaders() {
        context.getRequestHeaders().put("Authorization", "Bearer token");
        assertEquals("Bearer token", context.getRequestHeaders().get("Authorization"));
    }

    @Test
    void queryParams() {
        context.getQueryParams().put("id", "123");
        assertEquals("123", context.getQueryParams().get("id"));
    }

    @Test
    void setAndGetSchemaPaths() {
        context.setRequestSchemaPath("schemas/request.json");
        context.setResponseSchemaPath("schemas/response.json");
        assertEquals("schemas/request.json", context.getRequestSchemaPath());
        assertEquals("schemas/response.json", context.getResponseSchemaPath());
    }

    @Test
    void setAndGetSchemaTypes() {
        context.setRequestSchemaType(SchemaType.JSON_SCHEMA);
        context.setResponseSchemaType(SchemaType.XSD);
        assertEquals(SchemaType.JSON_SCHEMA, context.getRequestSchemaType());
        assertEquals(SchemaType.XSD, context.getResponseSchemaType());
    }

    @Test
    void setAndGetResponse() {
        assertNull(context.getResponse());
    }

    @Test
    void setAndGetBackendDelayMs() {
        context.setBackendDelayMs(2000);
        assertEquals(2000, context.getBackendDelayMs());
    }

    @Test
    void setAndGetBackendResponseHeaders() {
        Map<String, String> headers = Map.of("X-Custom", "value");
        context.setBackendResponseHeaders(headers);
        assertEquals(headers, context.getBackendResponseHeaders());
    }

    @Test
    void setAndGetBackendResponseContentType() {
        context.setBackendResponseContentType("application/xml");
        assertEquals("application/xml", context.getBackendResponseContentType());
    }

    @Test
    void resetClearsAllState() {
        context.setMethod("POST");
        context.setRequestBody("body");
        context.setRequestContentType("application/json");
        context.getRequestHeaders().put("key", "val");
        context.getQueryParams().put("q", "1");
        context.setRequestSchemaPath("req.json");
        context.setResponseSchemaPath("res.json");
        context.setRequestSchemaType(SchemaType.JSON_SCHEMA);
        context.setResponseSchemaType(SchemaType.XSD);
        context.setBackendDelayMs(100);
        context.setBackendResponseHeaders(Map.of("h", "v"));
        context.setBackendResponseContentType("text/plain");

        context.reset();

        assertNull(context.getMethod());
        assertNull(context.getRequestBody());
        assertNull(context.getRequestContentType());
        assertTrue(context.getRequestHeaders().isEmpty());
        assertTrue(context.getQueryParams().isEmpty());
        assertNull(context.getRequestSchemaPath());
        assertNull(context.getResponseSchemaPath());
        assertNull(context.getRequestSchemaType());
        assertNull(context.getResponseSchemaType());
        assertNull(context.getResponse());
        assertNull(context.getBackendDelayMs());
        assertNull(context.getBackendResponseHeaders());
        assertNull(context.getBackendResponseContentType());
    }
}
