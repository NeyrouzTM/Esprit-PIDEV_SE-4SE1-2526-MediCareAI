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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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

        Appointment result = appointmentService.create(dto, 1L, "PATIENT");

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
                () -> appointmentService.create(dto, 1L, "PATIENT"));
        assertEquals("Appointment date required", ex.getMessage());
    }

    @Test
    @DisplayName("expirePastPendingAppointments: mark expired and returns count")
    void expirePastPendingAppointments_marksExpired() {
        Appointment a1 = Appointment.builder().id(1L).status("PENDING").appointmentDate(LocalDateTime.now().minusHours(2)).build();
        Appointment a2 = Appointment.builder().id(2L).status("PENDING").appointmentDate(LocalDateTime.now().minusMinutes(30)).build();

        when(appointmentRepository.findByStatusAndAppointmentDateBefore(anyString(), any(LocalDateTime.class)))
                .thenReturn(List.of(a1, a2));

        int updated = appointmentService.expirePastPendingAppointments();

        assertEquals(2, updated);
        assertEquals("EXPIRED", a1.getStatus());
        assertEquals("EXPIRED", a2.getStatus());
        verify(appointmentRepository).saveAll(List.of(a1, a2));
    }

    @Test
    @DisplayName("searchByKeywords: patient role returns own appointments")
    void searchByKeywords_patientRole_returnsOwnAppointments() {
        Appointment a1 = new Appointment();
        a1.setId(11L);
        when(appointmentRepository.findByPatientId(7L)).thenReturn(List.of(a1));

        List<Appointment> result = appointmentService.searchByKeywords(2L, "ali", "check", 7L, "PATIENT");

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
    }
}
