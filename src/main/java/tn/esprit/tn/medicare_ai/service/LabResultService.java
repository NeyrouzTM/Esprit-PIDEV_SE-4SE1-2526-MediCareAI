package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.LabResultDTO;
import tn.esprit.tn.medicare_ai.entity.LabResult;
import java.util.List;

public interface LabResultService {
    LabResult create(LabResultDTO dto, Long currentUserId, String currentRole);
    LabResult getById(Long id, Long currentUserId, String currentRole);
    List<LabResult> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole);
    LabResult update(Long id, LabResultDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);
}