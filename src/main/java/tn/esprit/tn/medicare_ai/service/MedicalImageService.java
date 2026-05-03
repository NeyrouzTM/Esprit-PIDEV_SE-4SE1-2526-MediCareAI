package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.MedicalImageDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import java.util.List;

public interface MedicalImageService {
    MedicalImage create(MedicalImageDTO dto);
    MedicalImage getById(Long id);
    List<MedicalImage> getByMedicalRecordId(Long medicalRecordId);
    MedicalImage update(Long id, MedicalImageDTO dto);
    void delete(Long id);
}