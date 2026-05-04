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
import tn.esprit.tn.medicare_ai.entity.LabResult;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.LabResultService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LabResultControllerTest {

    @Mock
    private LabResultService labResultService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LabResultController labResultController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(labResultController).build();
    }

    @Test
    @DisplayName("GET /lab-results/{id} returns lab result")
    void getById_returnsLabResult() throws Exception {
        LabResult lab = new LabResult();
        lab.setId(1L);
        when(labResultService.getById(1L, 99L, "ADMIN")).thenReturn(lab);

        mockMvc.perform(get("/lab-results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /lab-results/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        LabResult lab = new LabResult();
        lab.setId(2L);
        when(labResultService.getByMedicalRecordId(3L, 99L, "ADMIN")).thenReturn(List.of(lab));

        mockMvc.perform(get("/lab-results/medical-record/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }
}

