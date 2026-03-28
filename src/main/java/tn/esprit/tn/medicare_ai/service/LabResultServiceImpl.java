package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.LabResultDTO;
import tn.esprit.tn.medicare_ai.entity.LabResult;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.LabResultRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabResultServiceImpl implements LabResultService {

    private final LabResultRepository labResultRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public LabResult create(LabResultDTO dto) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getTestName() == null || dto.getTestName().isBlank())
            throw new IllegalArgumentException("Test name required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));

        LabResult labResult = LabResult.builder()
                .medicalRecord(record)
                .testName(dto.getTestName())
                .result(dto.getResult())
                .unit(dto.getUnit())
                .normalRange(dto.getNormalRange())
                .testDate(dto.getTestDate())
                .notes(dto.getNotes())
                .build();

        return labResultRepository.save(labResult);
    }

    @Override
    public LabResult getById(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Lab result not found"));
    }

    @Override
    public List<LabResult> getByMedicalRecordId(Long medicalRecordId) {
        return labResultRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public LabResult update(Long id, LabResultDTO dto) {
        LabResult labResult = getById(id);
        if (dto.getTestName() != null)
            labResult.setTestName(dto.getTestName());
        if (dto.getResult() != null)
            labResult.setResult(dto.getResult());
        if (dto.getUnit() != null)
            labResult.setUnit(dto.getUnit());
        if (dto.getNormalRange() != null)
            labResult.setNormalRange(dto.getNormalRange());
        if (dto.getTestDate() != null)
            labResult.setTestDate(dto.getTestDate());
        if (dto.getNotes() != null)
            labResult.setNotes(dto.getNotes());
        return labResultRepository.save(labResult);
    }

    @Override
    public void delete(Long id) {
        LabResult labResult = getById(id);
        labResultRepository.delete(labResult);
    }
}