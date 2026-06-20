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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StepdefsTestConfig.class)
class WireMockStepsTest {

    @Autowired
    private WireMockSteps steps;

    @Autowired
    private WireMockManager wireMockManager;

    @Autowired
    private ScenarioContext context;

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
    void backendRespondsAfterDelaySetsContext() {
        steps.backendRespondsAfterDelay(2000);
        assertEquals(2000, context.getBackendDelayMs());
    }

    @Test
    void backendRespondsAfterDelayZero() {
        steps.backendRespondsAfterDelay(0);
        assertEquals(0, context.getBackendDelayMs());
    }

    @Test
    void backendRespondsWithHeadersSetsContext() {
        var headers = java.util.Map.of("X-Test", "value", "Content-Type", "application/xml");
        steps.backendRespondsWithHeaders(headers);
        assertEquals(headers, context.getBackendResponseHeaders());
    }

    @Test
    void backendRespondsWithContentTypeSetsContext() {
        steps.backendRespondsWithContentType("application/xml");
        assertEquals("application/xml", context.getBackendResponseContentType());
    }

    @Test
    void backendRespondsWithStatus() {
        steps.backendRespondsWithStatus(204);
    }

    @Test
    void backendRespondsWithStatusAndEmptyBody() {
        steps.backendRespondsWithStatusAndEmptyBody(200);
    }

    @Test
    void backendRespondsWithStatusAndBodyFromFile() {
        steps.backendRespondsWithStatusAndBodyFromFile(200, "test-payload.json");
    }

    @Test
    void backendRespondsWithStatusAndBodyInline() {
        steps.backendRespondsWithStatusAndBody(200, "{\"key\":\"value\"}");
    }
}
