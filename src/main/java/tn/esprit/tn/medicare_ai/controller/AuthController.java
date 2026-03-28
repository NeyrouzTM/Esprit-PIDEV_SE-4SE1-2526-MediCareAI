package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.CompleteRegistrationRequest;
import tn.esprit.tn.medicare_ai.dto.EmailVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.ForgotPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterWithVerificationRequest;
import tn.esprit.tn.medicare_ai.dto.ResetPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.VerifyEmailRequest;
import tn.esprit.tn.medicare_ai.service.IAuthService;

@Tag(name = "Authentication", description = "Auth endpoints for user registration, login, email verification, and password reset")
@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Simple registration without email verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User created: " + saved.getEmail());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password, returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // ==================== Email Verification Endpoints ====================

    @PostMapping("/register-with-verification")
    @Operation(summary = "Register with email verification", description = "Generate and send verification code to email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification code sent to email"),
        @ApiResponse(responseCode = "400", description = "Invalid email or already registered")
    })
    public ResponseEntity<EmailVerificationResponse> registerWithVerification(
            @Valid @RequestBody RegisterWithVerificationRequest req) {
        return ResponseEntity.ok(authService.registerWithEmailVerification(req));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify email with the code sent to user's email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired verification code")
    })
    public ResponseEntity<EmailVerificationResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        return ResponseEntity.ok(authService.verifyEmail(req));
    }

    @PostMapping("/complete-registration")
    @Operation(summary = "Complete registration", description = "Create user account after email verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or email already registered")
    })
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest req) {
        var user = authService.completeRegistration(req);
        return ResponseEntity.ok("User account created successfully: " + user.getEmail());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Generate and send password reset code to email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset code sent (if email exists)"),
        @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    public ResponseEntity<EmailVerificationResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        return ResponseEntity.ok(authService.forgotPassword(req));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using the code sent to email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset code")
    })
    public ResponseEntity<EmailVerificationResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        return ResponseEntity.ok(authService.resetPassword(req));
    }
}