package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    @DisplayName("create: valid dto sets default status PENDING")
    void create_validDto_setsPending() {
        User patient = new User();
        patient.setId(1L);
        patient.setRole(Role.PATIENT);
        User doctor = new User();
        doctor.setId(2L);
        doctor.setRole(Role.DOCTOR);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentDTO dto = AppointmentDTO.builder()
                .doctorId(2L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .reason("Follow-up")
                .build();

        Appointment result = appointmentService.create(dto, 1L);

        assertEquals("PENDING", result.getStatus());
        assertEquals(1L, result.getPatient().getId());
        assertEquals(2L, result.getDoctor().getId());
    }

    @Test
    @DisplayName("create: missing appointment date throws")
    void create_missingAppointmentDate_throws() {
        AppointmentDTO dto = AppointmentDTO.builder()
                .doctorId(2L)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.create(dto, 1L));
        assertEquals("Appointment date required", ex.getMessage());
    }
}
