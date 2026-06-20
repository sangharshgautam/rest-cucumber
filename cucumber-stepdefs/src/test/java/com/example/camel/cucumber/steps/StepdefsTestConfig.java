package com.example.camel.cucumber.steps;

import com.example.camel.cucumber.client.CamelRestClient;
import com.example.camel.cucumber.config.CamelCucumberProperties;
import com.example.camel.cucumber.context.ScenarioContext;
import com.example.camel.cucumber.schema.SchemaValidator;
import com.example.camel.cucumber.wiremock.WireMockManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepdefsTestConfig {

    private static final int TEST_PORT = 9097;

    @Bean
    public CamelCucumberProperties camelCucumberProperties() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setCamelUrl("http://localhost:" + TEST_PORT);
        props.setWiremockHost("localhost");
        props.setWiremockPort(TEST_PORT);
        props.setConnectionTimeoutMs(5000);
        props.setSocketTimeoutMs(10000);
        return props;
    }

    @Bean
    public WireMockManager wireMockManager(CamelCucumberProperties properties) {
        return new WireMockManager(properties);
    }

    @Bean
    public CamelRestClient camelRestClient(CamelCucumberProperties properties) {
        return new CamelRestClient(properties);
    }

    @Bean
    public SchemaValidator schemaValidator() {
        return new SchemaValidator();
    }

    @Bean
    public ScenarioContext scenarioContext() {
        return new ScenarioContext();
    }

    @Bean
    public WireMockSteps wireMockSteps() {
        return new WireMockSteps();
    }

    @Bean
    public RequestSteps requestSteps() {
        return new RequestSteps();
    }

    @Bean
    public RestActionSteps restActionSteps() {
        return new RestActionSteps();
    }

    @Bean
    public ResponseSteps responseSteps() {
        return new ResponseSteps();
    }
}
