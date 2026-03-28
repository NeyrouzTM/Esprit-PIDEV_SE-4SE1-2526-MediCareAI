package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import java.util.List;

public interface MedicalRecordService {
    MedicalRecord create(MedicalRecordDTO dto);
    MedicalRecord getById(Long id);
    MedicalRecord getByPatientId(Long patientId);
    List<MedicalRecord> getAll();
    MedicalRecord update(Long id, MedicalRecordDTO dto);
    void delete(Long id);
}
