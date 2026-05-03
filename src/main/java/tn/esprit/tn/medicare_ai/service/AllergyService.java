package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import java.util.List;

public interface AllergyService {
    Allergy create(AllergyDTO dto);
    Allergy getById(Long id);
    List<Allergy> getByMedicalRecordId(Long medicalRecordId);
    Allergy update(Long id, AllergyDTO dto);
    void delete(Long id);
}