package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.MedicalImageDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.MedicalImageRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalImageServiceImpl implements MedicalImageService {

    private final MedicalImageRepository medicalImageRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public MedicalImage create(MedicalImageDTO dto) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getImageType() == null || dto.getImageType().isBlank())
            throw new IllegalArgumentException("Image type required");
        if (dto.getImageUrl() == null || dto.getImageUrl().isBlank())
            throw new IllegalArgumentException("Image URL required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));

        MedicalImage image = MedicalImage.builder()
                .medicalRecord(record)
                .imageType(dto.getImageType())
                .imageUrl(dto.getImageUrl())
                .uploadDate(dto.getUploadDate())
                .description(dto.getDescription())
                .build();

        return medicalImageRepository.save(image);
    }

    @Override
    public MedicalImage getById(Long id) {
        return medicalImageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical image not found"));
    }

    @Override
    public List<MedicalImage> getByMedicalRecordId(Long medicalRecordId) {
        return medicalImageRepository
                .findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public MedicalImage update(Long id, MedicalImageDTO dto) {
        MedicalImage image = getById(id);
        if (dto.getImageType() != null)
            image.setImageType(dto.getImageType());
        if (dto.getImageUrl() != null)
            image.setImageUrl(dto.getImageUrl());
        if (dto.getUploadDate() != null)
            image.setUploadDate(dto.getUploadDate());
        if (dto.getDescription() != null)
            image.setDescription(dto.getDescription());
        return medicalImageRepository.save(image);
    }

    @Override
    public void delete(Long id) {
        MedicalImage image = getById(id);
        medicalImageRepository.delete(image);
    }
}