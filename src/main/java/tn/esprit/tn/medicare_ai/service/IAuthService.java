package tn.esprit.tn.medicare_ai.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.CompleteRegistrationRequest;
import tn.esprit.tn.medicare_ai.dto.EmailVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.ForgotPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterWithVerificationRequest;
import tn.esprit.tn.medicare_ai.dto.ResetPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.dto.VerifyEmailRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);

    EmailVerificationResponse registerWithEmailVerification(RegisterWithVerificationRequest req);
    EmailVerificationResponse verifyEmail(VerifyEmailRequest req);
    User completeRegistration(CompleteRegistrationRequest req);
    EmailVerificationResponse forgotPassword(ForgotPasswordRequest req);
    EmailVerificationResponse resetPassword(ResetPasswordRequest req);

    Page<UserResponse> getUsers(String query, Role role, Pageable pageable);
    Page<UserResponse> getDoctors(String query, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest req);
    void deleteUser(Long id);
}
