package tn.esprit.tn.medicare_ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the Medicare AI Backend API
 *
 * These tests verify end-to-end functionality with a real database connection.
 * They run during the CI/CD pipeline's integration test stage.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Medicare AI Integration Tests")
class ApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // If it reaches this point without exceptions, it passes
        assertTrue(port > 0);
    }

    @Test
    @DisplayName("OpenAPI docs endpoint should be accessible")
    void apiDocsEndpointShouldBeAccessible() throws Exception {
        HttpResponse<String> response = firstSuccessfulGet("/v3/api-docs", "/api-docs");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"paths\""));
    }

    @Test
    @DisplayName("Swagger UI endpoint should be accessible")
    void swaggerUiShouldBeAccessible() throws Exception {
        HttpResponse<String> response = firstSuccessfulGet("/swagger-ui/index.html", "/swagger-ui.html");

        assertEquals(200, response.statusCode());
    }

    private HttpResponse<String> firstSuccessfulGet(String... paths) throws IOException, InterruptedException {
        HttpResponse<String> lastResponse = null;
        for (String path : paths) {
            HttpResponse<String> response = get(path);
            if (response.statusCode() == 200) {
                return response;
            }
            lastResponse = response;
        }
        return lastResponse;
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        String normalizedContextPath = normalizeContextPath(contextPath);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + normalizedContextPath + path))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String normalizeContextPath(String rawContextPath) {
        if (rawContextPath == null || rawContextPath.isBlank() || "/".equals(rawContextPath)) {
            return "";
        }
        return rawContextPath.startsWith("/") ? rawContextPath : "/" + rawContextPath;
    }
}
