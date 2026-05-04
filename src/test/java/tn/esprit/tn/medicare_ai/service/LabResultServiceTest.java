package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.LabResultDTO;
import tn.esprit.tn.medicare_ai.entity.LabResult;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.LabResultRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabResultServiceTest {

    @Mock
    private LabResultRepository labResultRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private LabResultServiceImpl labResultService;

    @Test
    @DisplayName("create: valid dto saves lab result")
    void create_validDto_savesLabResult() {
        MedicalRecord record = new MedicalRecord();
        record.setId(4L);

        when(medicalRecordRepository.findById(4L)).thenReturn(Optional.of(record));
        when(labResultRepository.save(any(LabResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LabResultDTO dto = LabResultDTO.builder()
                .medicalRecordId(4L)
                .testName("CBC")
                .result("Normal")
                .build();

        LabResult result = labResultService.create(dto, 99L, "ADMIN");

        assertEquals("CBC", result.getTestName());
        assertEquals(4L, result.getMedicalRecord().getId());
    }

    @Test
    @DisplayName("create: missing test name throws")
    void create_missingTestName_throws() {
        LabResultDTO dto = LabResultDTO.builder()
                .medicalRecordId(4L)
                .testName(" ")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> labResultService.create(dto, 99L, "ADMIN"));
        assertEquals("Test name required", ex.getMessage());
    }
}
