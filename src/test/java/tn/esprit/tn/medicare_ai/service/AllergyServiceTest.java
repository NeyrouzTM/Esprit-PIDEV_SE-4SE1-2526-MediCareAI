package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.entity.Allergy;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllergyServiceTest {

    @Mock
    private AllergyRepository allergyRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private AllergyServiceImpl allergyService;

    @Test
    @DisplayName("create: valid dto saves allergy")
    void create_validDto_savesAllergy() {
        MedicalRecord record = new MedicalRecord();
        record.setId(3L);

        when(medicalRecordRepository.findById(3L)).thenReturn(Optional.of(record));
        when(allergyRepository.save(any(Allergy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AllergyDTO dto = AllergyDTO.builder()
                .medicalRecordId(3L)
                .allergyName("Peanuts")
                .severity("HIGH")
                .build();

        Allergy result = allergyService.create(dto, 99L, "ADMIN");

        assertEquals("Peanuts", result.getAllergyName());
        assertEquals("HIGH", result.getSeverity());
    }

    @Test
    @DisplayName("create: missing severity throws")
    void create_missingSeverity_throws() {
        AllergyDTO dto = AllergyDTO.builder()
                .medicalRecordId(3L)
                .allergyName("Peanuts")
                .severity(" ")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> allergyService.create(dto, 99L, "ADMIN"));
        assertEquals("Severity required", ex.getMessage());
    }
}
