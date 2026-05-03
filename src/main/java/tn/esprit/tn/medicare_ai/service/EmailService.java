package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.from:noreply@medicare-ai.com}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Medicare AI - Email Verification");
            message.setText(buildRegistrationEmailBody(code));

            getMailSender().send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Medicare AI - Password Reset");
            message.setText(buildPasswordResetEmailBody(code));

            getMailSender().send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildRegistrationEmailBody(String code) {
        return "Welcome to Medicare AI!\n\n" +
                "Your email verification code is: " + code + "\n\n" +
                "This code will expire in 15 minutes.\n\n" +
                "If you did not request this code, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Medicare AI Team";
    }

    private String buildPasswordResetEmailBody(String code) {
        return "Hello,\n\n" +
                "You requested a password reset for your Medicare AI account.\n\n" +
                "Your password reset code is: " + code + "\n\n" +
                "This code will expire in 15 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Medicare AI Team";
    }

    private JavaMailSender getMailSender() {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Mail sender is not configured. Set spring.mail.host and related mail properties.");
        }
        return mailSender;
    }
}

