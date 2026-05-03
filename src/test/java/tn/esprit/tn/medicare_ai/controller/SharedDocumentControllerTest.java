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
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SharedDocumentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@WithMockUser(username = "patient@med.com", roles = "PATIENT")
class SharedDocumentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private SharedDocumentService documentService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/collaboration/documents/sessions/{sessionId}: returns list of documents")
    void getDocumentsBySession_returnsList() throws Exception {
        SharedDocumentResponseDTO document = SharedDocumentResponseDTO.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileUrl("http://example.com/test.pdf")
                .uploaderId(1L)
                .uploaderName("Test Uploader")
                .sessionId(1L)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(documentService.getDocumentsBySession(1L)).thenReturn(List.of(document));

        mockMvc.perform(get("/api/collaboration/documents/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("test.pdf"));
    }

    @Test
    @DisplayName("GET /api/collaboration/documents/{id}: valid id returns document")
    void getDocumentById_validId_returnsDocument() throws Exception {
        SharedDocumentResponseDTO document = SharedDocumentResponseDTO.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileUrl("http://example.com/test.pdf")
                .uploaderId(1L)
                .uploaderName("Test Uploader")
                .sessionId(1L)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(documentService.getDocumentById(1L)).thenReturn(document);

        mockMvc.perform(get("/api/collaboration/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.pdf"));
    }
}




