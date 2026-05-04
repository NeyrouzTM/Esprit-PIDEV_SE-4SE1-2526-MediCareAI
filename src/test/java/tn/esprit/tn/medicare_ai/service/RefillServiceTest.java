package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.RefillRequestDto;
import tn.esprit.tn.medicare_ai.dto.response.RefillResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.exception.InvalidPrescriptionException;
import tn.esprit.tn.medicare_ai.exception.PrescriptionExpiredException;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.RefillRequestRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefillServiceTest {

    @Mock
    private RefillRequestRepository refillRequestRepository;
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefillService refillService;

    @Test
    @DisplayName("requestRefill: valid prescription creates pending refill")
    void requestRefill_validPrescription_createsPending() {
        User patient = user(1L);
        Prescription prescription = prescription(11L, patient, LocalDate.now().plusDays(5), PrescriptionStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(prescriptionRepository.findById(11L)).thenReturn(Optional.of(prescription));
        when(refillRequestRepository.save(any(RefillRequest.class))).thenAnswer(i -> {
            RefillRequest rr = i.getArgument(0);
            rr.setId(99L);
            return rr;
        });

        RefillRequestDto request = RefillRequestDto.builder()
                .prescriptionId(11L)
                .requestedQuantity(2)
                .build();

        RefillResponse response = refillService.requestRefill(request, 1L);

        assertEquals(99L, response.getId());
        assertEquals(RefillStatus.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("requestRefill: expired prescription throws PrescriptionExpiredException")
    void requestRefill_expiredPrescription_throws() {
        User patient = user(1L);
        Prescription prescription = prescription(11L, patient, LocalDate.now().minusDays(1), PrescriptionStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(prescriptionRepository.findById(11L)).thenReturn(Optional.of(prescription));

        RefillRequestDto request = RefillRequestDto.builder().prescriptionId(11L).requestedQuantity(1).build();

        assertThrows(PrescriptionExpiredException.class, () -> refillService.requestRefill(request, 1L));
    }

    @Test
    @DisplayName("requestRefill: invalid/cancelled prescription throws exception (already closed scenario)")
    void requestRefill_cancelledPrescription_throws() {
        User patient = user(1L);
        Prescription prescription = prescription(11L, patient, LocalDate.now().plusDays(2), PrescriptionStatus.CANCELLED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(prescriptionRepository.findById(11L)).thenReturn(Optional.of(prescription));

        RefillRequestDto request = RefillRequestDto.builder().prescriptionId(11L).requestedQuantity(1).build();

        assertThrows(InvalidPrescriptionException.class, () -> refillService.requestRefill(request, 1L));
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        return user;
    }

    private Prescription prescription(Long id, User patient, LocalDate expiryDate, PrescriptionStatus status) {
        Prescription p = new Prescription();
        p.setId(id);
        p.setPatient(patient);
        p.setExpiryDate(expiryDate);
        p.setStatus(status);
        return p;
    }
}



