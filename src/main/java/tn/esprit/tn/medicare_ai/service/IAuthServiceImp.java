package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.CompleteRegistrationRequest;
import tn.esprit.tn.medicare_ai.dto.EmailVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.ForgotPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterWithVerificationRequest;
import tn.esprit.tn.medicare_ai.dto.ResetPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.UserInfo;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.dto.VerifyEmailRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VerificationType;
import tn.esprit.tn.medicare_ai.exception.InvalidVerificationCodeException;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.security.CustomUserDetailsService;
import tn.esprit.tn.medicare_ai.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    @Override
    public User register(RegisterRequest req) {
        if (req.email() == null || req.email().isBlank())
            throw new IllegalArgumentException("Email required");

        if (userRepository.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException("Email already used");

        if (req.password() == null || req.password().length() < 6)
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters");

        if (req.role() == null)
            throw new IllegalArgumentException("Role required");

        User u = User.builder()
                .fullName(req.fullName() == null ?
                        "Not Available" : req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .enabled(true)
                .build();

        return userRepository.save(u);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        if (req == null || req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (req.password() == null || req.password().isBlank()) {
            throw new IllegalArgumentException("Password required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(), req.password())
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(req.email());

        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No roles found"))
                .getAuthority();

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

        return new AuthResponse(token, role, userDetails.getUsername(), userInfo);
    }

    @Override
    @Transactional
    public EmailVerificationResponse registerWithEmailVerification(RegisterWithVerificationRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String code = verificationCodeService.generateAndSaveVerificationCode(
                req.getEmail(),
                VerificationType.REGISTRATION
        );

        emailService.sendVerificationEmail(req.getEmail(), code);

        log.info("Registration verification code sent to: {}", req.getEmail());

        return EmailVerificationResponse.builder()
                .message("Verification code sent to your email. It will expire in 15 minutes.")
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public EmailVerificationResponse verifyEmail(VerifyEmailRequest req) {
        boolean isValid = verificationCodeService.verifyCode(
                req.getEmail(),
                req.getCode(),
                VerificationType.REGISTRATION
        );

        if (!isValid) {
            throw new InvalidVerificationCodeException("Invalid or expired verification code");
        }

        return EmailVerificationResponse.builder()
                .message("Email verified successfully. You can now complete your registration.")
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public User completeRegistration(CompleteRegistrationRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : Role.PATIENT)
                .enabled(true)
                .build();

        log.info("User registration completed for email: {}", req.getEmail());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public EmailVerificationResponse forgotPassword(ForgotPasswordRequest req) {
        var user = userRepository.findByEmail(req.getEmail());

        if (user.isEmpty()) {
            return EmailVerificationResponse.builder()
                    .message("If this email exists in our system, you will receive a password reset code.")
                    .success(true)
                    .build();
        }

        String code = verificationCodeService.generateAndSaveVerificationCode(
                req.getEmail(),
                VerificationType.PASSWORD_RESET
        );

        emailService.sendPasswordResetEmail(req.getEmail(), code);

        log.info("Password reset code sent to: {}", req.getEmail());

        return EmailVerificationResponse.builder()
                .message("If this email exists in our system, you will receive a password reset code.")
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public EmailVerificationResponse resetPassword(ResetPasswordRequest req) {
        boolean isValid = verificationCodeService.verifyCode(
                req.getEmail(),
                req.getCode(),
                VerificationType.PASSWORD_RESET
        );

        if (!isValid) {
            throw new InvalidVerificationCodeException("Invalid or expired reset code");
        }

        var user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", req.getEmail());

        return EmailVerificationResponse.builder()
                .message("Password reset successfully. You can now login with your new password.")
                .success(true)
                .build();
    }

    @Override
    public Page<UserResponse> getUsers(String query, Role role, Pageable pageable) {
        return userRepository.searchUsers(role, normalize(query), pageable)
                .map(this::toUserResponse);
    }

    @Override
    public Page<UserResponse> getDoctors(String query, Pageable pageable) {
        return userRepository.searchUsers(Role.DOCTOR, normalize(query), pageable)
                .map(this::toUserResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (req.fullName() != null && !req.fullName().isBlank()) {
            user.setFullName(req.fullName());
        }

        if (req.email() != null && !req.email().isBlank() && !req.email().equalsIgnoreCase(user.getEmail())) {
            User existing = userRepository.findByEmail(req.email()).orElse(null);
            if (existing != null && !existing.getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email already used");
            }
            user.setEmail(req.email());
        }

        if (req.password() != null && !req.password().isBlank()) {
            if (req.password().length() < 6) {
                throw new IllegalArgumentException("Password must contain at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(req.password()));
        }

        if (req.role() != null) {
            user.setRole(req.role());
        }

        if (req.enabled() != null) {
            user.setEnabled(req.enabled());
        }
        if (req.specialtyId() != null) {
            user.setSpecialtyId(req.specialtyId());
        }
        if (req.clinicalDepartment() != null) {
            user.setClinicalDepartment(req.clinicalDepartment());
        }
        if (req.clinicalKeywords() != null) {
            user.setClinicalKeywords(req.clinicalKeywords());
        }

        return toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getSpecialtyId(),
                user.getClinicalDepartment(),
                user.getClinicalKeywords()
        );
    }
}
