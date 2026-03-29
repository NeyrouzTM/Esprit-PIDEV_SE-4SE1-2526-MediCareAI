package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.UserIdResponse;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IAuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_returnsOkWithMessage() {
        User saved = new User();
        saved.setEmail("x@med.com");
        when(authService.register(org.mockito.ArgumentMatchers.any(RegisterRequest.class))).thenReturn(saved);

        ResponseEntity<?> response = controller.register(new RegisterRequest("X", "x@med.com", "pwd", Role.PATIENT));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("x@med.com"));
    }

    @Test
    void login_returnsAuthResponse() {
        AuthResponse auth = new AuthResponse("token", "x@med.com", "PATIENT");
        when(authService.login(new LoginRequest("x@med.com", "pwd"))).thenReturn(auth);

        ResponseEntity<AuthResponse> response = controller.login(new LoginRequest("x@med.com", "pwd"));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("token", response.getBody().token());
    }

    @Test
    void getUserId_returnsResolvedUser() {
        User user = new User();
        user.setId(7L);
        user.setEmail("x@med.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("x@med.com", null)
        );
        when(userRepository.findByEmail("x@med.com")).thenReturn(Optional.of(user));

        ResponseEntity<UserIdResponse> response = controller.getUserId();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(7L, response.getBody().id());
    }
}
