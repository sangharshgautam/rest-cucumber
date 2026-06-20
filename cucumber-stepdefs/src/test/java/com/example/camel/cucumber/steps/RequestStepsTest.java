package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaType;
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
class RequestStepsTest {

    @Autowired
    private RequestSteps steps;

    @Autowired
    private ScenarioContext context;

    @BeforeEach
    void setUp() {
        context.reset();
    }

    @Test
    void requestPayloadFromFileSetsBodyAndContentType() {
        steps.requestPayloadFromFile("test-payload.json");
        assertNotNull(context.getRequestBody());
        assertTrue(context.getRequestBody().contains("\"key\""));
        assertEquals("application/json", context.getRequestContentType());
    }

    @Test
    void requestPayloadFromFileDoesNotOverrideExistingContentType() {
        context.setRequestContentType("application/xml");
        steps.requestPayloadFromFile("test-payload.json");
        assertEquals("application/xml", context.getRequestContentType());
    }

    @Test
    void requestBodySetsBody() {
        steps.requestBody("hello world");
        assertEquals("hello world", context.getRequestBody());
    }

    @Test
    void requestBodyIsEmptySetsEmptyBody() {
        steps.requestBodyIsEmpty();
        assertEquals("", context.getRequestBody());
    }

    @Test
    void requestQueryParamAddsToMap() {
        steps.requestQueryParam("id", "123");
        assertEquals("123", context.getQueryParams().get("id"));
    }

    @Test
    void requestQueryParamsAddsAllToMap() {
        Map<String, String> params = Map.of("a", "1", "b", "2");
        steps.requestQueryParams(params);
        assertEquals("1", context.getQueryParams().get("a"));
        assertEquals("2", context.getQueryParams().get("b"));
    }

    @Test
    void requestHeaderAddsToMap() {
        steps.requestHeader("Authorization", "Bearer token");
        assertEquals("Bearer token", context.getRequestHeaders().get("Authorization"));
    }

    @Test
    void requestHeadersAddsAllToMap() {
        Map<String, String> headers = Map.of("X-A", "val1", "X-B", "val2");
        steps.requestHeaders(headers);
        assertEquals("val1", context.getRequestHeaders().get("X-A"));
        assertEquals("val2", context.getRequestHeaders().get("X-B"));
    }

    @Test
    void requestContentTypeSetsContentType() {
        steps.requestContentType("application/xml");
        assertEquals("application/xml", context.getRequestContentType());
    }

    @Test
    void requestSchemaAtSetsPathAndDetectsJsonType() {
        steps.requestSchemaAt("schemas/test-schema.json");
        assertEquals("schemas/test-schema.json", context.getRequestSchemaPath());
        assertEquals(SchemaType.JSON_SCHEMA, context.getRequestSchemaType());
    }

    @Test
    void requestSchemaAtSetsPathAndDetectsXsdType() {
        steps.requestSchemaAt("schemas/test-schema.xsd");
        assertEquals("schemas/test-schema.xsd", context.getRequestSchemaPath());
        assertEquals(SchemaType.XSD, context.getRequestSchemaType());
    }

    @Test
    void responseSchemaAtSetsPathAndDetectsJsonType() {
        steps.responseSchemaAt("schemas/test-schema.json");
        assertEquals("schemas/test-schema.json", context.getResponseSchemaPath());
        assertEquals(SchemaType.JSON_SCHEMA, context.getResponseSchemaType());
    }

    @Test
    void requestJsonSchemaAtSetsJsonTypeExplicitly() {
        steps.requestJsonSchemaAt("schemas/schema.json");
        assertEquals("schemas/schema.json", context.getRequestSchemaPath());
        assertEquals(SchemaType.JSON_SCHEMA, context.getRequestSchemaType());
    }

    @Test
    void responseJsonSchemaAtSetsJsonTypeExplicitly() {
        steps.responseJsonSchemaAt("schemas/schema.json");
        assertEquals("schemas/schema.json", context.getResponseSchemaPath());
        assertEquals(SchemaType.JSON_SCHEMA, context.getResponseSchemaType());
    }

    @Test
    void requestXsdSchemaAtSetsXsdTypeExplicitly() {
        steps.requestXsdSchemaAt("schemas/schema.xsd");
        assertEquals("schemas/schema.xsd", context.getRequestSchemaPath());
        assertEquals(SchemaType.XSD, context.getRequestSchemaType());
    }

    @Test
    void responseXsdSchemaAtSetsXsdTypeExplicitly() {
        steps.responseXsdSchemaAt("schemas/schema.xsd");
        assertEquals("schemas/schema.xsd", context.getResponseSchemaPath());
        assertEquals(SchemaType.XSD, context.getResponseSchemaType());
    }
}
