package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IAuthService;
import tn.esprit.tn.medicare_ai.service.PhysicianRecommendationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhysicianRecommendationService physicianRecommendationService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /auth/doctors returns doctors")
    void getDoctors_returnsPage() throws Exception {
        var doctors = new PageImpl<>(
                List.of(new UserResponse(2L, "Dr Strange", "doc@med.com", Role.DOCTOR, true)),
                PageRequest.of(0, 20),
                1
        );

        when(authService.getDoctors(eq("strange"), any())).thenReturn(doctors);

        mockMvc.perform(get("/auth/doctors").param("query", "strange"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].role").value("DOCTOR"))
                .andExpect(jsonPath("$.content[0].fullName").value("Dr Strange"));
    }

    @Test
    @DisplayName("GET /auth/users/{id} returns user")
    void getUserById_returnsUser() throws Exception {
        when(authService.getUserById(5L))
                .thenReturn(new UserResponse(5L, "John", "john@med.com", Role.PATIENT, true));

        mockMvc.perform(get("/auth/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value("john@med.com"));
    }

    @Test
    @DisplayName("PUT /auth/users/{id} updates user")
    void updateUser_returnsUpdated() throws Exception {
        UserUpdateRequest req = new UserUpdateRequest("Updated", null, null, null, true, null, null, null);

        when(authService.updateUser(eq(5L), any(UserUpdateRequest.class)))
                .thenReturn(new UserResponse(5L, "Updated", "john@med.com", Role.PATIENT, true));

        mockMvc.perform(put("/auth/users/5")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /auth/users/{id} returns no content")
    void deleteUser_returnsNoContent() throws Exception {
        doNothing().when(authService).deleteUser(5L);

        mockMvc.perform(delete("/auth/users/5"))
                .andExpect(status().isNoContent());
    }
}
