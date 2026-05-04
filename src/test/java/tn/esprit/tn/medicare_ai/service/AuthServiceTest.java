package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.security.CustomUserDetailsService;
import tn.esprit.tn.medicare_ai.security.jwt.JwtService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("getDoctors: returns paged doctors")
    void getDoctors_returnsDoctorsPage() {
        Pageable pageable = PageRequest.of(0, 10);

        User doctor = User.builder()
                .id(3L)
                .fullName("Dr House")
                .email("house@med.com")
                .role(Role.DOCTOR)
                .enabled(true)
                .build();

        Page<User> page = new PageImpl<>(List.of(doctor), pageable, 1);
        when(userRepository.searchUsers(Role.DOCTOR, "house", pageable)).thenReturn(page);

        Page<UserResponse> result = authService.getDoctors("house", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Dr House", result.getContent().get(0).fullName());
        assertEquals(Role.DOCTOR, result.getContent().get(0).role());
    }

    @Test
    @DisplayName("updateUser: updates email and password")
    void updateUser_updatesFields() {
        User existing = User.builder()
                .id(9L)
                .fullName("Old")
                .email("old@med.com")
                .password("old-pass")
                .role(Role.PATIENT)
                .enabled(true)
                .build();

        when(userRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@med.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("New Name", "new@med.com", "newpass123", Role.DOCTOR, true, null, null, null);

        UserResponse response = authService.updateUser(9L, req);

        assertEquals("New Name", response.fullName());
        assertEquals("new@med.com", response.email());
        assertEquals(Role.DOCTOR, response.role());
    }

    @Test
    @DisplayName("updateUser: throws when duplicate email")
    void updateUser_duplicateEmail_throws() {
        User existing = User.builder().id(1L).email("old@med.com").build();
        User other = User.builder().id(2L).email("used@med.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("used@med.com")).thenReturn(Optional.of(other));

        UserUpdateRequest req = new UserUpdateRequest(null, "used@med.com", null, null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(1L, req));
    }
}

