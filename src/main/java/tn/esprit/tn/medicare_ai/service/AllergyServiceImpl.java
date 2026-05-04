package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyServiceImpl implements AllergyService {

    private final AllergyRepository allergyRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public Allergy create(AllergyDTO dto, Long currentUserId, String currentRole) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getAllergyName() == null || dto.getAllergyName().isBlank())
            throw new IllegalArgumentException("Allergy name required");
        if (dto.getSeverity() == null || dto.getSeverity().isBlank())
            throw new IllegalArgumentException("Severity required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));

        ensureCanAccessRecord(record, currentUserId, currentRole);

        Allergy allergy = Allergy.builder()
                .medicalRecord(record)
                .allergyName(dto.getAllergyName())
                .severity(dto.getSeverity())
                .reaction(dto.getReaction())
                .notes(dto.getNotes())
                .build();

        return allergyRepository.save(allergy);
    }

    @Override
    public Allergy getById(Long id, Long currentUserId, String currentRole) {
        Allergy allergy = allergyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allergy not found"));
        ensureCanAccessRecord(allergy.getMedicalRecord(), currentUserId, currentRole);
        return allergy;
    }

    @Override
    public List<Allergy> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        ensureCanAccessRecord(record, currentUserId, currentRole);
        return allergyRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public Allergy update(Long id, AllergyDTO dto, Long currentUserId, String currentRole) {
        Allergy allergy = getById(id, currentUserId, currentRole);
        if (dto.getAllergyName() != null)
            allergy.setAllergyName(dto.getAllergyName());
        if (dto.getSeverity() != null)
            allergy.setSeverity(dto.getSeverity());
        if (dto.getReaction() != null)
            allergy.setReaction(dto.getReaction());
        if (dto.getNotes() != null)
            allergy.setNotes(dto.getNotes());
        return allergyRepository.save(allergy);
    }

    @Override
    public void delete(Long id, Long currentUserId, String currentRole) {
        Allergy allergy = getById(id, currentUserId, currentRole);
        allergyRepository.delete(allergy);
    }

    private void ensureCanAccessRecord(MedicalRecord record, Long currentUserId, String currentRole) {
        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            return;
        }
        if ("PATIENT".equals(currentRole) && record.getPatient().getId().equals(currentUserId)) {
            return;
        }
        throw new UnauthorizedActionException("You are not allowed to access this allergy data");
    }
}