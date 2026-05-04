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
import tn.esprit.tn.medicare_ai.entity.Allergy;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.AllergyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AllergyControllerTest {

    @Mock
    private AllergyService allergyService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AllergyController allergyController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(allergyController).build();
    }

    @Test
    @DisplayName("GET /allergies/{id} returns allergy")
    void getById_returnsAllergy() throws Exception {
        Allergy allergy = new Allergy();
        allergy.setId(6L);
        when(allergyService.getById(6L, 99L, "ADMIN")).thenReturn(allergy);

        mockMvc.perform(get("/allergies/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6));
    }

    @Test
    @DisplayName("GET /allergies/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        Allergy allergy = new Allergy();
        allergy.setId(7L);
        when(allergyService.getByMedicalRecordId(5L, 99L, "ADMIN")).thenReturn(List.of(allergy));

        mockMvc.perform(get("/allergies/medical-record/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));
    }
}
