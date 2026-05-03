package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.LabResult;
import java.util.List;

public interface LabResultRepository
        extends JpaRepository<LabResult, Long> {
    List<LabResult> findByMedicalRecordId(Long medicalRecordId);
}