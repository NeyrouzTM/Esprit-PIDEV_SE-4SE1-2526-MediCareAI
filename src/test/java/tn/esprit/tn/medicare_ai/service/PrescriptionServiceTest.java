package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    @Test
    @DisplayName("createPrescription: valid request saves prescription and returns response")
    void createPrescription_validRequest_savesAndReturns() {
        User patient = user(10L, "Patient", Role.PATIENT);
        User doctor = user(20L, "Doctor", Role.DOCTOR);
        Medicine med = medicine(1L, "Paracetamol");

        when(userRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(med));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PrescriptionRequest request = PrescriptionRequest.builder()
                .patientId(10L)
                .expiryDate(LocalDate.now().plusDays(10))
                .items(List.of(PrescriptionItemRequest.builder()
                        .medicineId(1L)
                        .quantity(2)
                        .dosage("500mg")
                        .frequency("Twice daily")
                        .durationDays(7)
                        .instructions("After meal")
                        .refills(1)
                        .build()))
                .build();

        PrescriptionDetailResponse response = prescriptionService.createPrescription(request, 20L);

        assertEquals(100L, response.getId());
        assertEquals(10L, response.getPatientId());
        assertEquals(20L, response.getDoctorId());
        assertEquals(1, response.getItems().size());
        assertEquals(PrescriptionStatus.ACTIVE, response.getStatus());
    }

    @Test
    @DisplayName("createPrescription: patient not found throws ResourceNotFoundException")
    void createPrescription_patientNotFound_throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        PrescriptionRequest request = PrescriptionRequest.builder()
                .patientId(999L)
                .expiryDate(LocalDate.now().plusDays(10))
                .items(List.of(PrescriptionItemRequest.builder()
                        .medicineId(1L)
                        .quantity(1)
                        .dosage("500mg")
                        .frequency("daily")
                        .durationDays(2)
                        .build()))
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.createPrescription(request, 20L));
    }

    @Test
    @DisplayName("createPrescription: medicine not found throws ResourceNotFoundException")
    void createPrescription_medicineNotFound_throws() {
        User patient = user(10L, "Patient", Role.PATIENT);
        User doctor = user(20L, "Doctor", Role.DOCTOR);

        when(userRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(medicineRepository.findById(55L)).thenReturn(Optional.empty());

        PrescriptionRequest request = PrescriptionRequest.builder()
                .patientId(10L)
                .expiryDate(LocalDate.now().plusDays(10))
                .items(List.of(PrescriptionItemRequest.builder()
                        .medicineId(55L)
                        .quantity(1)
                        .dosage("500mg")
                        .frequency("daily")
                        .durationDays(2)
                        .build()))
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.createPrescription(request, 20L));
    }

    private User user(Long id, String name, Role role) {
        User user = new User();
        user.setId(id);
        user.setFullName(name);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private Medicine medicine(Long id, String name) {
        Medicine m = new Medicine();
        m.setId(id);
        m.setName(name);
        m.setPrice(10.0);
        return m;
    }
}

