package com.example.camel.cucumber.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CamelCucumberPropertiesTest {

    @Test
    void defaults() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        assertEquals("http://localhost:8080", props.getCamelUrl());
        assertEquals("localhost", props.getWiremockHost());
        assertEquals(8089, props.getWiremockPort());
        assertEquals(5000, props.getConnectionTimeoutMs());
        assertEquals(10000, props.getSocketTimeoutMs());
    }

    @Test
    void setAndGetCamelUrl() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setCamelUrl("http://example.com:9090");
        assertEquals("http://example.com:9090", props.getCamelUrl());
    }

    @Test
    void setAndGetWiremockHost() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setWiremockHost("127.0.0.1");
        assertEquals("127.0.0.1", props.getWiremockHost());
    }

    @Test
    void setAndGetWiremockPort() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setWiremockPort(9999);
        assertEquals(9999, props.getWiremockPort());
    }

    @Test
    void setAndGetConnectionTimeoutMs() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setConnectionTimeoutMs(1000);
        assertEquals(1000, props.getConnectionTimeoutMs());
    }

    @Test
    void setAndGetSocketTimeoutMs() {
        CamelCucumberProperties props = new CamelCucumberProperties();
        props.setSocketTimeoutMs(5000);
        assertEquals(5000, props.getSocketTimeoutMs());
    }
}
