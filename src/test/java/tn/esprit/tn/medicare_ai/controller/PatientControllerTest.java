package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PatientController()).build();
    }

    @Test
    @DisplayName("GET /patient/hello returns greeting")
    void hello_returnsGreeting() throws Exception {
        mockMvc.perform(get("/patient/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello PATIENT !"));
    }
}

