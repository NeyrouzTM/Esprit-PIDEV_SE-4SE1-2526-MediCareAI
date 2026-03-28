package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.LabResultDTO;
import tn.esprit.tn.medicare_ai.entity.LabResult;
import java.util.List;

public interface LabResultService {
    LabResult create(LabResultDTO dto);
    LabResult getById(Long id);
    List<LabResult> getByMedicalRecordId(Long medicalRecordId);
    LabResult update(Long id, LabResultDTO dto);
    void delete(Long id);
}