package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.PrescriptionDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.Prescription;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Override
    public Prescription create(PrescriptionDTO dto) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");
        if (dto.getMedicationName() == null ||
                dto.getMedicationName().isBlank())
            throw new IllegalArgumentException("Medication name required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        Prescription prescription = Prescription.builder()
                .medicalRecord(record)
                .doctor(doctor)
                .medicationName(dto.getMedicationName())
                .dosage(dto.getDosage())
                .duration(dto.getDuration())
                .instructions(dto.getInstructions())
                .prescriptionDate(dto.getPrescriptionDate())
                .active(true)
                .build();

        return prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription getById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Prescription not found"));
    }

    @Override
    public List<Prescription> getByMedicalRecordId(Long medicalRecordId) {
        return prescriptionRepository
                .findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public Prescription update(Long id, PrescriptionDTO dto) {
        Prescription prescription = getById(id);
        if (dto.getMedicationName() != null)
            prescription.setMedicationName(dto.getMedicationName());
        if (dto.getDosage() != null)
            prescription.setDosage(dto.getDosage());
        if (dto.getDuration() != null)
            prescription.setDuration(dto.getDuration());
        if (dto.getInstructions() != null)
            prescription.setInstructions(dto.getInstructions());
        if (dto.getPrescriptionDate() != null)
            prescription.setPrescriptionDate(dto.getPrescriptionDate());
        prescription.setActive(dto.isActive());
        return prescriptionRepository.save(prescription);
    }

    @Override
    public void delete(Long id) {
        Prescription prescription = getById(id);
        prescriptionRepository.delete(prescription);
    }
}