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
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.MedicalImageService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MedicalImageControllerTest {

    @Mock
    private MedicalImageService medicalImageService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MedicalImageController medicalImageController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(medicalImageController).build();
    }

    @Test
    @DisplayName("GET /medical-images/{id} returns image")
    void getById_returnsImage() throws Exception {
        MedicalImage image = new MedicalImage();
        image.setId(4L);
        when(medicalImageService.getById(4L, 99L, "ADMIN")).thenReturn(image);

        mockMvc.perform(get("/medical-images/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    @DisplayName("GET /medical-images/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        MedicalImage image = new MedicalImage();
        image.setId(12L);
        when(medicalImageService.getByMedicalRecordId(2L, 99L, "ADMIN")).thenReturn(List.of(image));

        mockMvc.perform(get("/medical-images/medical-record/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12));
    }
}
