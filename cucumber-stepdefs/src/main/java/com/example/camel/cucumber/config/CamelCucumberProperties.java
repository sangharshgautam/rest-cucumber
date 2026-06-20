package com.example.camel.cucumber.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camel.cucumber")
public class CamelCucumberProperties {

    private String camelUrl = "http://localhost:8080";

    private String wiremockHost = "localhost";

    private int wiremockPort = 8089;

    private int connectionTimeoutMs = 5000;

    private int socketTimeoutMs = 10000;

    public String getCamelUrl() {
        return camelUrl;
    }

    public void setCamelUrl(String camelUrl) {
        this.camelUrl = camelUrl;
    }

    public String getWiremockHost() {
        return wiremockHost;
    }

    public void setWiremockHost(String wiremockHost) {
        this.wiremockHost = wiremockHost;
    }

    public int getWiremockPort() {
        return wiremockPort;
    }

    public void setWiremockPort(int wiremockPort) {
        this.wiremockPort = wiremockPort;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(int socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }
}
