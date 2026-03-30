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
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.service.IAuthService;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("GET /auth/users returns list")
    void getUsers_returnsOk() throws Exception {
        when(authService.getUsers()).thenReturn(List.of(
                new UserResponse(1L, "Admin", "admin@med.com", Role.ADMIN, true)
        ));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@med.com"));
    }

    @Test
    @DisplayName("GET /auth/users/{id} returns user details")
    void getUserById_returnsOk() throws Exception {
        when(authService.getUserById(3L))
                .thenReturn(new UserResponse(3L, "Doctor", "doctor@med.com", Role.DOCTOR, true));

        mockMvc.perform(get("/auth/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("doctor@med.com"));
    }

    @Test
    @DisplayName("PUT /auth/users/{id} updates user")
    void updateUser_returnsOk() throws Exception {
        UserUpdateRequest req = new UserUpdateRequest("Updated", "updated@med.com", null, Role.PATIENT, true);

        when(authService.updateUser(eq(4L), any(UserUpdateRequest.class)))
                .thenReturn(new UserResponse(4L, "Updated", "updated@med.com", Role.PATIENT, true));

        mockMvc.perform(put("/auth/users/4")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /auth/users/{id} returns no content")
    void deleteUser_returnsNoContent() throws Exception {
        doNothing().when(authService).deleteUser(6L);

        mockMvc.perform(delete("/auth/users/6"))
                .andExpect(status().isNoContent());
    }
}
