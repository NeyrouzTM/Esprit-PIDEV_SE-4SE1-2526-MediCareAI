package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import java.util.List;

public interface AllergyService {
    Allergy create(AllergyDTO dto, Long currentUserId, String currentRole);
    Allergy getById(Long id, Long currentUserId, String currentRole);
    List<Allergy> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole);
    Allergy update(Long id, AllergyDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);
}