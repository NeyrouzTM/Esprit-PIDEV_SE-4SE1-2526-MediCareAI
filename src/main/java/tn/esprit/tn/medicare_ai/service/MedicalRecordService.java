package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import java.util.List;

public interface MedicalRecordService {
    MedicalRecord create(MedicalRecordDTO dto, Long currentUserId);
    MedicalRecord getById(Long id, Long currentUserId, String currentRole);
    MedicalRecord getByPatientId(Long patientId, Long currentUserId, String currentRole);
    List<MedicalRecord> getAll(String currentRole, Long currentUserId);
    MedicalRecord update(Long id, MedicalRecordDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);
}
