package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.MedicalImageDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalImage;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.MedicalImageRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalImageServiceTest {

    @Mock
    private MedicalImageRepository medicalImageRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalImageServiceImpl medicalImageService;

    @Test
    @DisplayName("create: valid dto saves medical image")
    void create_validDto_savesImage() {
        MedicalRecord record = new MedicalRecord();
        record.setId(7L);

        when(medicalRecordRepository.findById(7L)).thenReturn(Optional.of(record));
        when(medicalImageRepository.save(any(MedicalImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicalImageDTO dto = MedicalImageDTO.builder()
                .medicalRecordId(7L)
                .imageType("XRAY")
                .imageUrl("https://cdn/xray1.png")
                .uploadDate(LocalDate.now())
                .build();

        MedicalImage result = medicalImageService.create(dto);

        assertEquals("XRAY", result.getImageType());
        assertEquals(7L, result.getMedicalRecord().getId());
    }

    @Test
    @DisplayName("create: missing image url throws")
    void create_missingImageUrl_throws() {
        MedicalImageDTO dto = MedicalImageDTO.builder()
                .medicalRecordId(7L)
                .imageType("XRAY")
                .imageUrl(" ")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> medicalImageService.create(dto));
        assertEquals("Image URL required", ex.getMessage());
    }
}

