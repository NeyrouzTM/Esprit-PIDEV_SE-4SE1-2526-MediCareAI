package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.CollaborationSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "patient@med.com", roles = "PATIENT")
class CollaborationSessionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private CollaborationSessionService sessionService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/collaboration/sessions: returns list of sessions")
    void getAllSessions_returnsList() throws Exception {
        CollaborationSessionResponseDTO session = CollaborationSessionResponseDTO.builder()
                .id(1L)
                .title("Test Session")
                .creatorId(1L)
                .creatorName("Test Creator")
                .createdAt(LocalDateTime.now())
                .build();

        when(sessionService.getAllSessions()).thenReturn(List.of(session));

        mockMvc.perform(get("/api/collaboration/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Session"));
    }

    @Test
    @DisplayName("GET /api/collaboration/sessions/{id}: valid id returns session")
    void getSessionById_validId_returnsSession() throws Exception {
        CollaborationSessionResponseDTO session = CollaborationSessionResponseDTO.builder()
                .id(1L)
                .title("Test Session")
                .creatorId(1L)
                .creatorName("Test Creator")
                .createdAt(LocalDateTime.now())
                .build();

        when(sessionService.getSessionById(1L)).thenReturn(session);

        mockMvc.perform(get("/api/collaboration/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Session"));
    }
}













