package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import tn.esprit.tn.medicare_ai.config.OpenApiConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiDocsTest {

    @Test
    void openApiConfig_buildsExpectedMetadata() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.medicareOpenApi();

        assertNotNull(openAPI);
        assertEquals("Medicare AI API", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion());
    }
}
