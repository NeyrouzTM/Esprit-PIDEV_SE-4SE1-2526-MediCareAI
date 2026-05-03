package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Override
    public MedicalRecord create(MedicalRecordDTO dto) {
        if (dto.getPatientId() == null)
            throw new IllegalArgumentException("Patient ID required");

        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        if (medicalRecordRepository.findByPatient(patient).isPresent())
            throw new IllegalArgumentException(
                    "Medical record already exists for this patient");

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
    public MedicalRecord getById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));
    }

    @Override
    public MedicalRecord getByPatientId(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));
        return medicalRecordRepository.findByPatient(patient)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));
    }

    @Override
    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }

    @Override
    public MedicalRecord update(Long id, MedicalRecordDTO dto) {
        MedicalRecord record = getById(id);
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
    public void delete(Long id) {
        MedicalRecord record = getById(id);
        medicalRecordRepository.delete(record);
    }
}