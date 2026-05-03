package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import java.util.Optional;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByPatient(User patient);
}