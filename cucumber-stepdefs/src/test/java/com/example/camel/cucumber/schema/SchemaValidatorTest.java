package com.example.camel.cucumber.schema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class SchemaValidatorTest {

    private final SchemaValidator validator = new SchemaValidator();

    @Test
    void detectSchemaTypeJson() {
        assertEquals(SchemaType.JSON_SCHEMA, SchemaValidator.detectSchemaType("schema.json"));
    }

    @Test
    void detectSchemaTypeXsd() {
        assertEquals(SchemaType.XSD, SchemaValidator.detectSchemaType("schema.xsd"));
    }

    @Test
    void detectSchemaTypeNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> SchemaValidator.detectSchemaType(null));
    }

    @Test
    void detectSchemaTypeUnknownThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> SchemaValidator.detectSchemaType("schema.txt"));
    }

    @Test
    void detectContentTypeJson() {
        assertEquals("application/json", SchemaValidator.detectContentType("file.json"));
    }

    @Test
    void detectContentTypeXml() {
        assertEquals("application/xml", SchemaValidator.detectContentType("file.xml"));
    }

    @Test
    void detectContentTypeTxt() {
        assertEquals("text/plain", SchemaValidator.detectContentType("file.txt"));
    }

    @Test
    void detectContentTypeHtml() {
        assertEquals("text/html", SchemaValidator.detectContentType("file.html"));
        assertEquals("text/html", SchemaValidator.detectContentType("file.htm"));
    }

    @Test
    void detectContentTypeNull() {
        assertEquals("application/octet-stream", SchemaValidator.detectContentType(null));
    }

    @Test
    void detectContentTypeUnknown() {
        assertEquals("application/octet-stream", SchemaValidator.detectContentType("file.bin"));
    }

    @Test
    void loadFileContentSuccess() {
        String content = SchemaValidator.loadFileContent("test-payload.json");
        assertEquals("{\"key\": \"value\"}\n", content);
    }

    @Test
    void loadFileContentThrowsOnMissing() {
        assertThrows(RuntimeException.class,
                () -> SchemaValidator.loadFileContent("nonexistent.file"));
    }

    @Test
    void validateJsonValid() {
        String payload = "{\"name\": \"John\", \"age\": 30}";
        SchemaValidator.ValidationResult result = validator.validateJson(payload, "test-schema.json");
        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void validateJsonInvalid() {
        String payload = "{\"name\": \"John\"}";
        SchemaValidator.ValidationResult result = validator.validateJson(payload, "test-schema.json");
        assertFalse(result.isValid());
        assertFalse(result.errors().isEmpty());
    }

    @Test
    void validateJsonInvalidType() {
        String payload = "{\"name\": \"John\", \"age\": \"old\"}";
        SchemaValidator.ValidationResult result = validator.validateJson(payload, "test-schema.json");
        assertFalse(result.isValid());
    }

    @Test
    void validateJsonThrowsOnInvalidSchemaPath() {
        assertThrows(RuntimeException.class,
                () -> validator.validateJson("{}", "nonexistent.json"));
    }

    @Test
    void validateXmlValid() {
        String payload = "<?xml version=\"1.0\"?><person><name>John</name><age>30</age></person>";
        SchemaValidator.ValidationResult result = validator.validateXml(payload, "test-schema.xsd");
        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void validateXmlInvalid() {
        String payload = "<?xml version=\"1.0\"?><person><name>John</name></person>";
        SchemaValidator.ValidationResult result = validator.validateXml(payload, "test-schema.xsd");
        assertFalse(result.isValid());
        assertFalse(result.errors().isEmpty());
    }

    @Test
    void validateXmlThrowsOnInvalidSchemaPath() {
        assertThrows(RuntimeException.class,
                () -> validator.validateXml("<root/>", "nonexistent.xsd"));
    }

    @Test
    void validateDelegatesJson() {
        String payload = "{\"name\": \"John\", \"age\": 30}";
        SchemaValidator.ValidationResult result = validator.validate(payload, "test-schema.json", SchemaType.JSON_SCHEMA);
        assertTrue(result.isValid());
    }

    @Test
    void validateDelegatesXsd() {
        String payload = "<?xml version=\"1.0\"?><person><name>John</name><age>30</age></person>";
        SchemaValidator.ValidationResult result = validator.validate(payload, "test-schema.xsd", SchemaType.XSD);
        assertTrue(result.isValid());
    }

    @Test
    void validationResultRecord() {
        SchemaValidator.ValidationResult pass = new SchemaValidator.ValidationResult(true, java.util.List.of());
        assertTrue(pass.isValid());
        assertTrue(pass.errors().isEmpty());

        SchemaValidator.ValidationResult fail = new SchemaValidator.ValidationResult(false, java.util.List.of("error1"));
        assertFalse(fail.isValid());
        assertEquals(1, fail.errors().size());
        assertEquals("error1", fail.errors().get(0));
    }
}
