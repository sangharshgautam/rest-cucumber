package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.wiremock.WireMockManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
public class Hooks {

    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

    @Autowired
    private WireMockManager wireMockManager;

    @Autowired
    private ScenarioContext scenarioContext;

    @BeforeAll
    public static void beforeAll() {
        LOG.info("========================================");
        LOG.info("Starting Camel Cucumber E2E Test Suite");
        LOG.info("========================================");
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        LOG.info("--- Scenario: {} ---", scenario.getName());
        wireMockManager.start();
        wireMockManager.resetStubs();
        scenarioContext.reset();
    }

    @After
    public void afterScenario(Scenario scenario) {
        LOG.info("--- Scenario '{}' finished: {} ---", scenario.getName(), scenario.getStatus());
    }
}
