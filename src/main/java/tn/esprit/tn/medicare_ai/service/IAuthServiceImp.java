package tn.esprit.tn.medicare_ai.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tn.esprit.tn.medicare_ai.dto.VerifyEmailRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VerificationType;
import tn.esprit.tn.medicare_ai.exception.InvalidVerificationCodeException;
import tn.esprit.tn.medicare_ai.exception.VerificationCodeExpiredException;
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

        return new AuthResponse(token, userDetails.getUsername(), role);
    }

    @Override
    @Transactional
    public EmailVerificationResponse registerWithEmailVerification(RegisterWithVerificationRequest req) {
        // Validate email not already in use
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Generate and send verification code
        String code = verificationCodeService.generateAndSaveVerificationCode(
                req.getEmail(),
                VerificationType.REGISTRATION
        );

        // Send email with code
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
        // Verify the code
        boolean isValid = verificationCodeService.verifyCode(
                req.getEmail(),
                req.getCode(),
                VerificationType.REGISTRATION
        );

        if (!isValid) {
            throw new InvalidVerificationCodeException("Invalid or expired verification code");
        }

        // Code is valid - we can now proceed with account creation in next step
        return EmailVerificationResponse.builder()
                .message("Email verified successfully. You can now complete your registration.")
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public User completeRegistration(CompleteRegistrationRequest req) {
        // Validate email not already registered
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate password
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }

        // Create and save user
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
        // Check if user exists
        var user = userRepository.findByEmail(req.getEmail());

        if (user.isEmpty()) {
            // Don't reveal if email exists or not for security reasons
            return EmailVerificationResponse.builder()
                    .message("If this email exists in our system, you will receive a password reset code.")
                    .success(true)
                    .build();
        }

        // Generate and send reset code
        String code = verificationCodeService.generateAndSaveVerificationCode(
                req.getEmail(),
                VerificationType.PASSWORD_RESET
        );

        // Send email with reset code
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
        // Verify the reset code
        boolean isValid = verificationCodeService.verifyCode(
                req.getEmail(),
                req.getCode(),
                VerificationType.PASSWORD_RESET
        );

        if (!isValid) {
            throw new InvalidVerificationCodeException("Invalid or expired reset code");
        }

        // Find user and update password
        var user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", req.getEmail());

        return EmailVerificationResponse.builder()
                .message("Password reset successfully. You can now login with your new password.")
                .success(true)
                .build();
    }
}
