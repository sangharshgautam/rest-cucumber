# Camel Cucumber E2E Test Framework

Reusable Cucumber step definitions for testing **Camel REST services** with **WireMock**-backed backend mocking, supporting **JSON Schema** and **XSD** validation.

## Architecture

```
Cucumber Test  ──HTTP──►  Camel REST API  ──►  WireMock (mock backend)
     │                         │                     │
     │                         │                     │
     ▼                         ▼                     ▼
Step Def Library          JSON Schema / XSD       Response
(cucumber-stepdefs)       Validation              Verification
```

The Camel service is assumed to be **externally deployed**. Tests call it via HTTP. WireMock runs alongside the tests as a mock for the downstream backend system.

## Modules

| Module | Artifact | Description |
|---|---|---|
| `cucumber-stepdefs` | `com.example.camel.cucumber:cucumber-stepdefs` | Reusable step definitions library (JAR) |
| `cucumber-tests` | `com.example.camel.cucumber:cucumber-tests` | Sample test scenarios |

## Prerequisites

- Java 17+
- Maven 3.8+

## Build

```bash
mvn clean install
```

## Configuration

Set via system properties or `application.properties`:

| Property | Default | Description |
|---|---|---|
| `camel.cucumber.camel-url` | `http://localhost:8080` | URL of the deployed Camel REST service |
| `camel.cucumber.wiremock-host` | `localhost` | WireMock bind address |
| `camel.cucumber.wiremock-port` | `8089` | WireMock server port |
| `camel.cucumber.connection-timeout-ms` | `5000` | HTTP connection timeout |
| `camel.cucumber.socket-timeout-ms` | `10000` | HTTP socket read timeout |

```bash
mvn test -Dcamel.cucumber.camel-url=http://my-camel-host:8080 -Dcamel.cucumber.wiremock-port=9099
```

## Usage

### Add the Library to Your Project

```xml
<dependency>
    <groupId>com.example.camel.cucumber</groupId>
    <artifactId>cucumber-stepdefs</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### Configure Cucumber

Set the glue package in your runner or `junit-platform.properties`:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.example.camel.cucumber")
public class CucumberRunnerTest {
}
```

### Create a Feature File

```gherkin
Feature: Process Orders

  Background:
    Given the request schema is at "schemas/order-request.json"
    And the response schema is at "schemas/order-response.json"

  Scenario: Successfully process a valid request
    Given the backend will respond with status 200 and body from file "wiremock/__files/success.json"
    And the request payload from file "payloads/valid-order.json"
    When I send a POST request to "/api/process"
    Then the response status should be 200
    And the response body matches the response schema
    And the backend was called exactly 1 time(s)
```

### Supported HTTP Methods

`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`

## Project Structure

```
rest-cucumber/
├── pom.xml
├── cucumber-stepdefs/                    ← Reusable library
│   ├── pom.xml
│   └── src/main/java/com/example/camel/cucumber/
│       ├── config/                        ← Spring config + properties
│       ├── context/                       ← Per-scenario shared state
│       ├── client/                        ← REST Assured HTTP client
│       ├── wiremock/                      ← WireMock lifecycle manager
│       ├── schema/                        ← JSON Schema + XSD validator
│       └── steps/                         ← Cucumber step definitions
├── cucumber-tests/                        ← Sample test scenarios
│   └── src/test/
│       ├── java/.../tests/CucumberRunnerTest.java
│       └── resources/
│           ├── features/
│           ├── schemas/
│           ├── payloads/
│           └── wiremock/__files/
├── CLAUDE.md
└── README.md
```

## Extending

To override default configuration, add `application.properties` to your test classpath:

```properties
camel.cucumber.camel-url=http://localhost:9090
camel.cucumber.wiremock-port=9099
```

To add custom step definitions that use the shared `ScenarioContext`, annotate your class with `@Component` and inject `ScenarioContext`, `WireMockManager`, `CamelRestClient`, or `SchemaValidator` via `@Autowired`.
