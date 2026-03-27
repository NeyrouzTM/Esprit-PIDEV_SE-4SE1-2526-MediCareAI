package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import java.util.List;

public interface MedicalImageRepository
        extends JpaRepository<MedicalImage, Long> {
    List<MedicalImage> findByMedicalRecordId(Long medicalRecordId);
}