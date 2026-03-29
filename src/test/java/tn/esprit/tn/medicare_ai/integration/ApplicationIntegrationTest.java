package tn.esprit.tn.medicare_ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Medicare AI Backend API
 *
 * These tests verify end-to-end functionality with a real database connection.
 * They run during the CI/CD pipeline's integration test stage.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Medicare AI Integration Tests")
class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize test data if needed
    }

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // This test simply verifies the application context loads
        // If it reaches this point without exceptions, it passes
    }

    @Test
    @DisplayName("Health endpoint should return UP status")
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("API documentation endpoint should be accessible")
    void apiDocsEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").exists())
                .andExpect(jsonPath("$.paths").exists());
    }

    @Test
    @DisplayName("Swagger UI should be accessible")
    void swaggerUiShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Actuator metrics endpoint should return metrics")
    void actuatorMetricsShouldReturnData() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }

    @Test
    @DisplayName("Actuator info endpoint should return application info")
    void actuatorInfoShouldReturnAppInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    // Add more integration tests for your specific API endpoints
    // Example:
    // @Test
    // @DisplayName("GET /api/health-events should return list")
    // void getHealthEventsShouldReturnList() throws Exception {
    //     mockMvc.perform(get("/api/health-events"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$").isArray());
    // }
}

