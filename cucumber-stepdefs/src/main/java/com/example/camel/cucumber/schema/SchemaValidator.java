package com.example.camel.cucumber.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchemaValidator {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaValidator.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ValidationResult validate(String payload, String schemaClasspathPath, SchemaType type) {
        if (type == SchemaType.XSD) {
            return validateXml(payload, schemaClasspathPath);
        }
        return validateJson(payload, schemaClasspathPath);
    }

    public ValidationResult validateJson(String payload, String schemaClasspathPath) {
        try {
            String schemaContent = loadFileContent(schemaClasspathPath);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaContent);

            JsonNode jsonNode = objectMapper.readTree(payload);
            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (errors.isEmpty()) {
                LOG.info("JSON Schema validation passed for: {}", schemaClasspathPath);
                return new ValidationResult(true, List.of());
            }

            List<String> errorMessages = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .toList();
            LOG.warn("JSON Schema validation failed for: {} - errors: {}", schemaClasspathPath, errorMessages);
            return new ValidationResult(false, errorMessages);

        } catch (Exception e) {
            throw new RuntimeException("Failed to validate JSON against schema: " + schemaClasspathPath, e);
        }
    }

    public ValidationResult validateXml(String payload, String schemaClasspathPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(
                    new StreamSource(new ClassPathResource(schemaClasspathPath).getInputStream()));

            Validator validator = schema.newValidator();
            List<String> errors = new ArrayList<>();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    errors.add("WARNING: " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    errors.add("ERROR (line " + e.getLineNumber() + "): " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) {
                    errors.add("FATAL (line " + e.getLineNumber() + "): " + e.getMessage());
                }
            });

            validator.validate(new StreamSource(new StringReader(payload)));

            if (errors.isEmpty()) {
                LOG.info("XSD validation passed for: {}", schemaClasspathPath);
                return new ValidationResult(true, List.of());
            }

            LOG.warn("XSD validation failed for: {} - errors: {}", schemaClasspathPath, errors);
            return new ValidationResult(false, errors);

        } catch (SAXException e) {
            throw new RuntimeException("Invalid XSD schema: " + schemaClasspathPath, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read XSD schema: " + schemaClasspathPath, e);
        }
    }

    public static SchemaType detectSchemaType(String schemaPath) {
        if (schemaPath == null) {
            throw new IllegalArgumentException("Schema path must not be null");
        }
        if (schemaPath.endsWith(".xsd")) {
            return SchemaType.XSD;
        }
        if (schemaPath.endsWith(".json")) {
            return SchemaType.JSON_SCHEMA;
        }
        throw new IllegalArgumentException(
                "Cannot detect schema type from path: " + schemaPath
                        + ". File must end with .json or .xsd");
    }

    public static String loadFileContent(String classpathPath) {
        try {
            return new String(new ClassPathResource(classpathPath).getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file from classpath: " + classpathPath, e);
        }
    }

    public static String detectContentType(String filePath) {
        if (filePath == null) return "application/octet-stream";
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".xml")) return "application/xml";
        if (filePath.endsWith(".txt")) return "text/plain";
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) return "text/html";
        return "application/octet-stream";
    }

    public record ValidationResult(boolean isValid, List<String> errors) {
    }
}
