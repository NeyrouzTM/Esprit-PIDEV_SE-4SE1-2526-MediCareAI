package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitNoteServiceTest {

    @Mock
    private VisitNoteRepository visitNoteRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VisitNoteServiceImpl visitNoteService;

    @Test
    @DisplayName("create: valid dto saves visit note as not finalized")
    void create_validDto_savesVisitNote() {
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        User doctor = new User();
        doctor.setId(2L);

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(visitNoteRepository.save(any(VisitNote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VisitNoteDTO dto = VisitNoteDTO.builder()
                .medicalRecordId(1L)
                .doctorId(2L)
                .visitDate(LocalDateTime.now())
                .subjective("Headache")
                .build();

        VisitNote result = visitNoteService.create(dto, 99L, "ADMIN");

        assertEquals(1L, result.getMedicalRecord().getId());
        assertEquals(2L, result.getDoctor().getId());
        assertEquals(false, result.isFinalized());
    }

    @Test
    @DisplayName("delete: finalized note throws")
    void delete_finalizedNote_throws() {
        VisitNote note = new VisitNote();
        note.setId(5L);
        note.setFinalized(true);

        when(visitNoteRepository.findById(5L)).thenReturn(Optional.of(note));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> visitNoteService.delete(5L, 99L, "ADMIN"));
        assertEquals("Cannot delete a finalized visit note", ex.getMessage());
    }
}

