package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.PrescriptionDTO;
import tn.esprit.tn.medicare_ai.entity.Prescription;
import java.util.List;

public interface PrescriptionService {
    Prescription create(PrescriptionDTO dto);
    Prescription getById(Long id);
    List<Prescription> getByMedicalRecordId(Long medicalRecordId);
    Prescription update(Long id, PrescriptionDTO dto);
    void delete(Long id);
}