package tn.esprit.tn.medicare_ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("Health endpoint should return UP status")
    void healthEndpointShouldReturnUp() throws Exception {
        HttpResponse<String> response = get("/actuator/health");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    @Test
    @DisplayName("API documentation endpoint should be accessible")
    void apiDocsEndpointShouldBeAccessible() throws Exception {
        HttpResponse<String> response = get("/api-docs");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"info\""));
        assertTrue(response.body().contains("\"paths\""));
    }

    @Test
    @DisplayName("Swagger UI should be accessible")
    void swaggerUiShouldBeAccessible() throws Exception {
        HttpResponse<String> response = get("/swagger-ui.html");

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Actuator metrics endpoint should return metrics")
    void actuatorMetricsShouldReturnData() throws Exception {
        HttpResponse<String> response = get("/actuator/metrics");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"names\""));
    }

    @Test
    @DisplayName("Actuator info endpoint should return application info")
    void actuatorInfoShouldReturnAppInfo() throws Exception {
        HttpResponse<String> response = get("/actuator/info");

        assertEquals(200, response.statusCode());
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
