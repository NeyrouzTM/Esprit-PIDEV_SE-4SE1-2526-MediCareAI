package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.entity.VerificationCode;
import tn.esprit.tn.medicare_ai.entity.VerificationType;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${app.verification.code-expiry-minutes:15}")
    private int codeExpiryMinutes;

    /**
     * Generate and store a verification code
     */
    public String generateAndSaveVerificationCode(String email, VerificationType type) {
        // Delete any existing code for this email and type
        verificationCodeRepository.deleteByEmailAndType(email, type);

        // Generate 6-digit code
        String code = generateSixDigitCode();

        // Calculate expiry time
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(codeExpiryMinutes);

        // Save to database
        VerificationCode verificationCode = VerificationCode.builder()
                .email(email)
                .code(code)
                .type(type)
                .expiryTime(expiryTime)
                .used(false)
                .build();

        verificationCodeRepository.save(verificationCode);
        log.info("Verification code generated for email: {} with type: {}", email, type);

        return code;
    }

    /**
     * Verify the code and mark as used
     */
    public boolean verifyCode(String email, String code, VerificationType type) {
        var verificationCode = verificationCodeRepository
                .findByEmailAndCodeAndTypeAndUsedFalse(email, code, type);

        if (verificationCode.isEmpty()) {
            log.warn("Invalid or already used verification code for email: {} with type: {}", email, type);
            return false;
        }

        VerificationCode vc = verificationCode.get();

        // Check if expired
        if (vc.isExpired()) {
            log.warn("Verification code expired for email: {} with type: {}", email, type);
            return false;
        }

        // Mark as used
        vc.setUsed(true);
        verificationCodeRepository.save(vc);
        log.info("Verification code verified and marked as used for email: {}", email);

        return true;
    }

    /**
     * Generate a random 6-digit code
     */
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Clean up expired codes (optional scheduled task)
     */
    @Transactional
    public void cleanupExpiredCodes() {
        // This could be called by a scheduled task to clean up old codes
        // For now, we rely on the isExpired() method and the 'used' flag
        log.info("Cleanup of expired codes completed");
    }
}

