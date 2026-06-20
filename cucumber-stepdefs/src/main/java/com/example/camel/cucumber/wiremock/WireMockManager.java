package com.example.camel.cucumber.wiremock;

import com.example.camel.cucumber.config.CamelCucumberProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockManager {

    private static final Logger LOG = LoggerFactory.getLogger(WireMockManager.class);

    private final WireMockServer wireMockServer;

    private final List<StubMapping> scenarioStubs = new ArrayList<>();

    public WireMockManager(CamelCucumberProperties properties) {
        this.wireMockServer = new WireMockServer(wireMockConfig()
                .port(properties.getWiremockPort())
                .bindAddress(properties.getWiremockHost()));
    }

    public void start() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            LOG.info("WireMock started on port {}", wireMockServer.port());
        }
        WireMock.configureFor(wireMockServer.port());
    }

    public void stop() {
        if (wireMockServer.isRunning()) {
            LOG.info("Stopping WireMock");
            wireMockServer.stop();
        }
    }

    public void resetStubs() {
        LOG.info("Resetting WireMock stubs");
        wireMockServer.resetAll();
        scenarioStubs.clear();
    }

    public StubMapping createStub(int status, String body, String contentType,
                                   Map<String, String> headers, Integer delayMs) {
        var responseBuilder = aResponse()
                .withStatus(status)
                .withBody(body != null ? body : "");

        if (contentType != null) {
            responseBuilder.withHeader("Content-Type", contentType);
        } else {
            responseBuilder.withHeader("Content-Type", "application/json");
        }

        if (headers != null) {
            headers.forEach(responseBuilder::withHeader);
        }

        if (delayMs != null && delayMs > 0) {
            responseBuilder.withFixedDelay(delayMs);
        }

        StubMapping stub = stubFor(any(anyUrl())
                .willReturn(responseBuilder));

        scenarioStubs.add(stub);
        LOG.info("Created WireMock stub: status={}, bodyLength={}", status,
                body != null ? body.length() : 0);
        return stub;
    }

    public StubMapping createStub(int status, String body, String contentType) {
        return createStub(status, body, contentType, null, null);
    }

    public StubMapping createStub(int status, String body) {
        return createStub(status, body, "application/json", null, null);
    }

    public void verifyCalled(int times) {
        int actual = wireMockServer.countRequestsMatching(RequestPatternBuilder.allRequests().build()).getCount();
        if (actual != times) {
            throw new AssertionError("Expected backend to be called " + times
                    + " time(s), but was called " + actual + " time(s)");
        }
        LOG.info("Backend was called exactly {} time(s)", times);
    }

    public void verifyAtLeast(int times) {
        int actual = wireMockServer.countRequestsMatching(RequestPatternBuilder.allRequests().build()).getCount();
        if (actual < times) {
            throw new AssertionError("Expected backend to be called at least " + times
                    + " time(s), but was called only " + actual + " time(s)");
        }
        LOG.info("Backend was called at least {} time(s) (actual: {})", times, actual);
    }

    public void verifyNeverCalled() {
        int actual = wireMockServer.countRequestsMatching(RequestPatternBuilder.allRequests().build()).getCount();
        if (actual > 0) {
            throw new AssertionError("Expected backend to never be called, but was called "
                    + actual + " time(s)");
        }
        LOG.info("Backend was never called");
    }
}
