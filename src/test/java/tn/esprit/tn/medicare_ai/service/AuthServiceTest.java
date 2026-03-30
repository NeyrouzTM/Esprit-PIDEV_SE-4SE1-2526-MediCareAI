package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.security.CustomUserDetailsService;
import tn.esprit.tn.medicare_ai.security.jwt.JwtService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private IAuthServiceImp authService;

    @Test
    @DisplayName("getUsers: maps users without exposing password")
    void getUsers_returnsMappedResponse() {
        User u = User.builder()
                .id(1L)
                .fullName("Admin User")
                .email("admin@med.com")
                .password("secret")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserResponse> users = authService.getUsers();

        assertEquals(1, users.size());
        assertEquals("Admin User", users.get(0).fullName());
        assertEquals("admin@med.com", users.get(0).email());
        assertEquals(Role.ADMIN, users.get(0).role());
        assertTrue(users.get(0).enabled());
    }

    @Test
    @DisplayName("getUserById: missing id throws ResourceNotFoundException")
    void getUserById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.getUserById(99L));
    }

    @Test
    @DisplayName("updateUser: updates mutable fields and encodes password")
    void updateUser_validRequest_updatesUser() {
        User existing = User.builder()
                .id(7L)
                .fullName("Old Name")
                .email("old@med.com")
                .password("old-encoded")
                .role(Role.PATIENT)
                .enabled(true)
                .build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@med.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest(
                "New Name",
                "new@med.com",
                "newpass123",
                Role.DOCTOR,
                false
        );

        UserResponse updated = authService.updateUser(7L, req);

        assertEquals("New Name", updated.fullName());
        assertEquals("new@med.com", updated.email());
        assertEquals(Role.DOCTOR, updated.role());
        assertFalse(updated.enabled());
    }

    @Test
    @DisplayName("updateUser: duplicate email throws IllegalArgumentException")
    void updateUser_duplicateEmail_throws() {
        User existing = User.builder()
                .id(7L)
                .fullName("User")
                .email("old@med.com")
                .password("encoded")
                .role(Role.PATIENT)
                .enabled(true)
                .build();

        User conflict = User.builder().id(9L).email("used@med.com").build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("used@med.com")).thenReturn(Optional.of(conflict));

        UserUpdateRequest req = new UserUpdateRequest(
                null,
                "used@med.com",
                null,
                null,
                null
        );

        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(7L, req));
    }

    @Test
    @DisplayName("deleteUser: existing id calls repository delete")
    void deleteUser_existing_deletes() {
        when(userRepository.existsById(5L)).thenReturn(true);

        authService.deleteUser(5L);

        verify(userRepository).deleteById(5L);
    }
}
