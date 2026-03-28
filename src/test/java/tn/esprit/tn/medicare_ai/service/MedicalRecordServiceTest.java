package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    @Test
    @DisplayName("create: valid dto saves medical record")
    void create_validDto_savesRecord() {
        User patient = new User();
        patient.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatient(patient)).thenReturn(Optional.empty());
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        MedicalRecordDTO dto = MedicalRecordDTO.builder()
                .patientId(10L)
                .bloodType("O+")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        MedicalRecord result = medicalRecordService.create(dto);

        assertEquals(100L, result.getId());
        assertEquals("O+", result.getBloodType());
        assertEquals(10L, result.getPatient().getId());
    }

    @Test
    @DisplayName("create: duplicate patient record throws")
    void create_duplicatePatient_throws() {
        User patient = new User();
        patient.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatient(patient)).thenReturn(Optional.of(new MedicalRecord()));

        MedicalRecordDTO dto = MedicalRecordDTO.builder().patientId(10L).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.create(dto));
        assertEquals("Medical record already exists for this patient", ex.getMessage());
    }
}
