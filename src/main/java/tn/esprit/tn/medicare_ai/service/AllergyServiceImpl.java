package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyServiceImpl implements AllergyService {

    private final AllergyRepository allergyRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public Allergy create(AllergyDTO dto) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getAllergyName() == null || dto.getAllergyName().isBlank())
            throw new IllegalArgumentException("Allergy name required");
        if (dto.getSeverity() == null || dto.getSeverity().isBlank())
            throw new IllegalArgumentException("Severity required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));

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
    public Allergy getById(Long id) {
        return allergyRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Allergy not found"));
    }

    @Override
    public List<Allergy> getByMedicalRecordId(Long medicalRecordId) {
        return allergyRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public Allergy update(Long id, AllergyDTO dto) {
        Allergy allergy = getById(id);
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
    public void delete(Long id) {
        Allergy allergy = getById(id);
        allergyRepository.delete(allergy);
    }
}