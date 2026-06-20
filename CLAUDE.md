# CLAUDE.md — Project Guide

## Project Overview

Multi-module Maven project providing reusable Cucumber step definitions for E2E testing of Camel REST APIs with WireMock-backed backend mocking.

## Modules

| Module | Purpose |
|---|---|
| `cucumber-stepdefs` | Reusable step definitions library (JAR artifact) |
| `cucumber-tests` | Test scenarios consuming the stepdefs library |

## Key Architecture

```
Cucumber Test  ──HTTP──►  Camel REST API  ──►  WireMock (mock backend)
     │                         │                     │
     │                         │                     │
     ▼                         ▼                     ▼
Step Def Library          JSON Schema / XSD       Response
(cucumber-stepdefs)       Validation              Verification
```

## Build Commands

```bash
# Full build (compile + test)
mvn clean install

# Skip tests during build
mvn clean install -DskipTests

# Run only cucumber-tests
mvn test -pl cucumber-tests -am

# Run with specific Camel URL
mvn test -pl cucumber-tests -am -Dcamel.cucumber.camel-url=http://localhost:9090

# Run with specific WireMock port
mvn test -pl cucumber-tests -am -Dcamel.cucumber.wiremock-port=9099
```

## Configuration Properties

| Property | Default | Description |
|---|---|---|
| `camel.cucumber.camel-url` | `http://localhost:8080` | URL of the deployed Camel REST service |
| `camel.cucumber.wiremock-host` | `localhost` | WireMock bind address |
| `camel.cucumber.wiremock-port` | `8089` | WireMock server port |
| `camel.cucumber.connection-timeout-ms` | `5000` | HTTP connection timeout |
| `camel.cucumber.socket-timeout-ms` | `10000` | HTTP socket read timeout |

## Adding the Library to Another Project

```xml
<dependency>
    <groupId>com.example.camel.cucumber</groupId>
    <artifactId>cucumber-stepdefs</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

The glue package for Cucumber must be set to `com.example.camel.cucumber`.

## Step Definitions Reference

### Given — WireMock Backend Setup

```
the backend will respond with status {int} and body from file "{filepath}"
the backend will respond with status {int} and body:
  (docstring)
the backend will respond with status {int} and empty body
the backend will respond with status {int}
the backend will respond after a delay of {int}ms
the backend will respond with headers:
  | name | value |
the backend will respond with content type "{mimeType}"
```

### Given — Request Setup

```
the request payload from file "{filepath}"           — file body, auto-detect content type
the request body: (docstring)                         — inline body (ad-hoc)
the request body is empty
the request query parameter "{name}" is "{value}"
the request query parameters: (table)
the request header "{name}" is "{value}"
the request headers: (table)
the request content type is "{mimeType}"
the request schema is at "{filepath}"                 — auto-detect .json vs .xsd
the response schema is at "{filepath}"                — auto-detect .json vs .xsd
the request JSON schema is at "{filepath}"            — explicit JSON Schema
the response JSON schema is at "{filepath}"           — explicit JSON Schema
the request XSD schema is at "{filepath}"             — explicit XSD
the response XSD schema is at "{filepath}"            — explicit XSD
```

### When — HTTP Action

```
I send a {method} request to "{path}"                 — GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
I send a {method} request to "{path}" with query parameters: (table)
I send the request to "{path}"                        — uses pre-configured method (default POST)
```

### Then — Assertions

```
the response status should be {int}
the response status is not {int}
the response body matches the response schema
the response body matches the request schema
the response body validates against the schema at "{filepath}"
the request body validates against the request schema
the response body is: (docstring)
the response body contains "{text}"
the response body matches JSON path "{path}" is "{value}"
the response body XML matches XPath "{path}" is "{value}"
the response header "{name}" should be "{value}"
the response content type should be "{mimeType}"
the response time is less than {int}ms
the backend was called exactly {int} time(s)
the backend was called at least {int} time(s)
the backend was never called
```

## File Conventions

Files referenced in steps (payloads, schemas, wiremock responses) are loaded from the **classpath**.
Conventional directory layout under `src/test/resources/`:

```
src/test/resources/
├── features/           ← .feature files
├── payloads/           ← request/response payloads (.json, .xml)
├── schemas/            ← JSON Schema (.json) and XSD (.xsd)
└── wiremock/
    └── __files/        ← mock backend response bodies
```

Content type is **auto-detected from file extension**:
- `.json` → `application/json`
- `.xml` → `application/xml`
- Override with `Given the request content type is "..."` step
