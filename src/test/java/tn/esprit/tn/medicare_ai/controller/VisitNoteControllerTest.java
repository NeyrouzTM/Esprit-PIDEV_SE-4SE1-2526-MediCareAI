package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.service.VisitNoteService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VisitNoteControllerTest {

    @Mock
    private VisitNoteService visitNoteService;

    @InjectMocks
    private VisitNoteController visitNoteController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(visitNoteController).build();
    }

    @Test
    @DisplayName("GET /visit-notes/{id} returns visit note")
    void getById_returnsVisitNote() throws Exception {
        VisitNote note = new VisitNote();
        note.setId(3L);
        when(visitNoteService.getById(3L)).thenReturn(note);

        mockMvc.perform(get("/visit-notes/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("GET /visit-notes/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        VisitNote note = new VisitNote();
        note.setId(8L);
        when(visitNoteService.getByMedicalRecordId(12L)).thenReturn(List.of(note));

        mockMvc.perform(get("/visit-notes/medical-record/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(8));
    }
}

