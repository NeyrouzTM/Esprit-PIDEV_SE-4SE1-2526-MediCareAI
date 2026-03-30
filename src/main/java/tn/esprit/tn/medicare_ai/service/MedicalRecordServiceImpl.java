package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Override
    public MedicalRecord create(MedicalRecordDTO dto, Long currentUserId) {
        User patient = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated patient not found"));

        if (patient.getRole() != Role.PATIENT) {
            throw new UnauthorizedActionException("Only patients can create their medical record");
        }

        if (medicalRecordRepository.findByPatient(patient).isPresent())
            throw new IllegalArgumentException("Medical record already exists for this patient");

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .bloodType(dto.getBloodType())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .dateOfBirth(dto.getDateOfBirth())
                .medicalHistory(dto.getMedicalHistory())
                .chronicDiseases(dto.getChronicDiseases())
                .build();

        return medicalRecordRepository.save(record);
    }

    @Override
    public MedicalRecord getById(Long id, Long currentUserId, String currentRole) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        ensureCanAccessRecord(record, currentUserId, currentRole);
        return record;
    }

    @Override
    public MedicalRecord getByPatientId(Long patientId, Long currentUserId, String currentRole) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        MedicalRecord record = medicalRecordRepository.findByPatient(patient)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));

        ensureCanAccessRecord(record, currentUserId, currentRole);
        return record;
    }

    @Override
    public List<MedicalRecord> getAll(String currentRole, Long currentUserId) {
        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            return medicalRecordRepository.findAll();
        }
        return List.of(getByPatientId(currentUserId, currentUserId, currentRole));
    }

    @Override
    public MedicalRecord update(Long id, MedicalRecordDTO dto, Long currentUserId, String currentRole) {
        MedicalRecord record = getById(id, currentUserId, currentRole);

        if (dto.getBloodType() != null)
            record.setBloodType(dto.getBloodType());
        if (dto.getHeight() != null)
            record.setHeight(dto.getHeight());
        if (dto.getWeight() != null)
            record.setWeight(dto.getWeight());
        if (dto.getDateOfBirth() != null)
            record.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getMedicalHistory() != null)
            record.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getChronicDiseases() != null)
            record.setChronicDiseases(dto.getChronicDiseases());

        return medicalRecordRepository.save(record);
    }

    @Override
    public void delete(Long id, Long currentUserId, String currentRole) {
        MedicalRecord record = getById(id, currentUserId, currentRole);
        medicalRecordRepository.delete(record);
    }

    private void ensureCanAccessRecord(MedicalRecord record, Long currentUserId, String currentRole) {
        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            return;
        }
        if ("PATIENT".equals(currentRole) && record.getPatient().getId().equals(currentUserId)) {
            return;
        }
        throw new UnauthorizedActionException("You are not allowed to access this medical record");
    }
}