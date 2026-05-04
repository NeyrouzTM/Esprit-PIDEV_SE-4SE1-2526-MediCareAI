package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.MedicalImageDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import java.util.List;

public interface MedicalImageService {
    MedicalImage create(MedicalImageDTO dto, Long currentUserId, String currentRole);
    MedicalImage getById(Long id, Long currentUserId, String currentRole);
    List<MedicalImage> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole);
    MedicalImage update(Long id, MedicalImageDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);
}