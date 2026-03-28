package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.VerificationCode;
import tn.esprit.tn.medicare_ai.entity.VerificationType;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmailAndCodeAndTypeAndUsedFalse(String email, String code, VerificationType type);
    Optional<VerificationCode> findByEmailAndType(String email, VerificationType type);
    void deleteByEmailAndType(String email, VerificationType type);
}

