package tn.esprit.tn.medicare_ai.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.tn.medicare_ai.dto.PrescriptionDTO;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.request.UploadPrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionResponse;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionVerificationResponse;
import tn.esprit.tn.medicare_ai.entity.Prescription;

import java.util.List;

public interface PrescriptionService {
    Prescription create(PrescriptionDTO dto);

    Prescription getById(Long id);

    List<Prescription> getByMedicalRecordId(Long medicalRecordId);

    Prescription update(Long id, PrescriptionDTO dto);

    void delete(Long id);

    PrescriptionDetailResponse createPrescription(PrescriptionRequest request, Long doctorId);

    Page<PrescriptionResponse> getPrescriptionsByPatient(Long patientId, Pageable pageable);

    Page<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorId, Pageable pageable);

    PrescriptionDetailResponse getPrescriptionById(Long id);

    PrescriptionVerificationResponse uploadPrescription(UploadPrescriptionRequest request, Long patientId);
}