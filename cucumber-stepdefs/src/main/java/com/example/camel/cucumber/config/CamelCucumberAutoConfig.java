package com.example.camel.cucumber.config;

import com.example.camel.cucumber.client.CamelRestClient;
import com.example.camel.cucumber.schema.SchemaValidator;
import com.example.camel.cucumber.wiremock.WireMockManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CamelCucumberProperties.class)
@ComponentScan(basePackages = "com.example.camel.cucumber")
public class CamelCucumberAutoConfig {

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
}
