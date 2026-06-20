Feature: Camel REST Process API

  As a client of the Camel REST service
  I want to send requests that are validated against a schema
  And have the service forward valid requests to a backend

  Background:
    Given the request schema is at "schemas/request-schema.json"
    And the response schema is at "schemas/response-schema.json"

  @smoke
  Scenario: Successfully process a valid JSON request
    Given the backend will respond with status 200 and body from file "wiremock/__files/success-response.json"
    And the request payload from file "payloads/valid-request.json"
    When I send a POST request to "/api/process"
    Then the response status should be 200
    And the response body matches the response schema
    And the backend was called exactly 1 time(s)

  @negative
  Scenario: Reject a request with missing required fields
    Given the request payload from file "payloads/invalid-request.json"
    When I send a POST request to "/api/process"
    Then the response status should be 400
    And the backend was never called

  @negative
  Scenario: Reject a request with invalid payload format
    Given the request payload from file "payloads/valid-request.json"
    And the request header "Content-Type" is "application/xml"
    When I send a POST request to "/api/process"
    Then the response status should be 400
    And the backend was never called

  @smoke
  Scenario: Successfully retrieve data via GET
    Given the backend will respond with status 200 and body from file "wiremock/__files/success-response.json"
    And the request query parameter "id" is "ORD-001"
    When I send a GET request to "/api/orders"
    Then the response status should be 200
    And the backend was called exactly 1 time(s)

  @smoke
  Scenario: Backend returns error gracefully
    Given the backend will respond with status 500 and body:
      """
      {"error": "internal_error", "message": "Backend unavailable"}
      """
    And the request payload from file "payloads/valid-request.json"
    When I send a POST request to "/api/process"
    Then the response status should be 500
    And the backend was called exactly 1 time(s)

  @smoke
  Scenario: Handle backend with delayed response
    Given the backend will respond after a delay of 2000ms
    And the backend will respond with status 200 and body from file "wiremock/__files/success-response.json"
    And the request payload from file "payloads/valid-request.json"
    When I send a POST request to "/api/process"
    Then the response status should be 200
    And the response time is less than 5000ms

  @smoke
  Scenario: Send request and verify backend headers
    Given the backend will respond with status 200 and body from file "wiremock/__files/success-response.json"
    And the backend will respond with headers:
      | X-Correlation-Id | test-123 |
    And the request payload from file "payloads/valid-request.json"
    When I send a POST request to "/api/process"
    Then the response status should be 200
    And the response header "X-Correlation-Id" should be "test-123"

  @xml
  Scenario: Successfully process a valid XML request with XSD validation
    Given the request XSD schema is at "schemas/order-request.xsd"
    And the response XSD schema is at "schemas/order-response.xsd"
    And the request content type is "application/xml"
    And the backend will respond with status 200 and body from file "wiremock/__files/success-response.xml"
    And the request payload from file "payloads/valid-request.xml"
    When I send a POST request to "/api/orders"
    Then the response status should be 200
    And the response body matches the response schema
    And the backend was called exactly 1 time(s)

  @xml @negative
  Scenario: Reject XML request that fails XSD validation
    Given the request XSD schema is at "schemas/order-request.xsd"
    And the request content type is "application/xml"
    And the request payload from file "payloads/invalid-request.xml"
    When I send a POST request to "/api/orders"
    Then the response status should be 400
    And the backend was never called
