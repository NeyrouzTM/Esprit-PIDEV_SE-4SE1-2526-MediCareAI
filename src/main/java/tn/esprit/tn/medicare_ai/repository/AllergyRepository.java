package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import java.util.List;

public interface AllergyRepository
        extends JpaRepository<Allergy, Long> {
    List<Allergy> findByMedicalRecordId(Long medicalRecordId);
}