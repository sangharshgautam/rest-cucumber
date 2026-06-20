package com.example.camel.cucumber;

import com.example.camel.cucumber.config.CamelCucumberAutoConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CamelCucumberAutoConfig.class)
public class CucumberSpringConfiguration {
}
