package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.VisitNoteService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VisitNoteControllerTest {

    @Mock
    private VisitNoteService visitNoteService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VisitNoteController visitNoteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        User currentUser = new User();
        currentUser.setId(99L);
        currentUser.setRole(Role.ADMIN);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null)
        );
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));

        mockMvc = MockMvcBuilders.standaloneSetup(visitNoteController).build();
    }

    @Test
    @DisplayName("GET /visit-notes/{id} returns visit note")
    void getById_returnsVisitNote() throws Exception {
        VisitNote note = new VisitNote();
        note.setId(3L);
        when(visitNoteService.getById(3L, 99L, "ADMIN")).thenReturn(note);

        mockMvc.perform(get("/visit-notes/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("GET /visit-notes/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        VisitNote note = new VisitNote();
        note.setId(8L);
        when(visitNoteService.getByMedicalRecordId(12L, 99L, "ADMIN")).thenReturn(List.of(note));

        mockMvc.perform(get("/visit-notes/medical-record/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(8));
    }

    @Test
    @DisplayName("GET /visit-notes/search returns list")
    void searchClinicalNotes_returnsList() throws Exception {
        VisitNote note = new VisitNote();
        note.setId(15L);
        when(visitNoteService.searchClinicalNotes(eq("ali"), eq("house"), eq("fever"), eq(99L), eq("ADMIN")))
                .thenReturn(List.of(note));

        mockMvc.perform(get("/visit-notes/search")
                        .param("patientKeyword", "ali")
                        .param("doctorKeyword", "house")
                        .param("clinicalKeyword", "fever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15));
    }
}
