package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.CompleteRegistrationRequest;
import tn.esprit.tn.medicare_ai.dto.EmailVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.ForgotPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterWithVerificationRequest;
import tn.esprit.tn.medicare_ai.dto.ResetPasswordRequest;
import tn.esprit.tn.medicare_ai.dto.VerifyEmailRequest;
import tn.esprit.tn.medicare_ai.entity.User;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);

    // Email verification endpoints
    EmailVerificationResponse registerWithEmailVerification(RegisterWithVerificationRequest req);
    EmailVerificationResponse verifyEmail(VerifyEmailRequest req);
    User completeRegistration(CompleteRegistrationRequest req);
    EmailVerificationResponse forgotPassword(ForgotPasswordRequest req);
    EmailVerificationResponse resetPassword(ResetPasswordRequest req);
}