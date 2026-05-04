package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Appointment create(AppointmentDTO dto, Long currentUserId, String currentRole) {
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");
        if (dto.getAppointmentDate() == null)
            throw new IllegalArgumentException("Appointment date required");

        User patient = resolvePatientForNewAppointment(dto, currentUserId, currentRole);

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (doctor.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Selected user is not a doctor");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(dto.getAppointmentDate())
                .appointmentEndDate(dto.getEndTime())
                .status("PENDING")
                .reason(dto.getReason())
                .consultationType(dto.getConsultationType())
                .notes(dto.getNotes())
                .build();

        return appointmentRepository.save(appointment);
    }

    /**
     * PATIENT books as themselves. ADMIN/DOCTOR book on behalf of {@link AppointmentDTO#getPatientId()}.
     */
    private User resolvePatientForNewAppointment(AppointmentDTO dto, Long currentUserId, String currentRole) {
        if ("PATIENT".equals(currentRole)) {
            User patient = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Authenticated patient not found"));
            if (patient.getRole() != Role.PATIENT) {
                throw new UnauthorizedActionException("Only patients can create appointments as a patient");
            }
            if (dto.getPatientId() != null && !dto.getPatientId().equals(currentUserId)) {
                throw new UnauthorizedActionException("Patients cannot book for another user");
            }
            return patient;
        }
        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            if (dto.getPatientId() == null) {
                throw new IllegalArgumentException("Patient ID required when creating as staff");
            }
            User patient = userRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
            if (patient.getRole() != Role.PATIENT) {
                throw new IllegalArgumentException("Selected user is not a patient");
            }
            return patient;
        }
        throw new UnauthorizedActionException("Not allowed to create appointments");
    }

    @Override
    public Appointment getById(Long id, Long currentUserId, String currentRole) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        ensureCanAccessAppointment(appointment, currentUserId, currentRole);
        return appointment;
    }

    @Override
    public List<Appointment> getAll(String currentRole, Long currentUserId) {
        if ("ADMIN".equals(currentRole)) {
            return appointmentRepository.findAll();
        }
        if ("DOCTOR".equals(currentRole)) {
            return appointmentRepository.findByDoctorId(currentUserId);
        }
        return appointmentRepository.findByPatientId(currentUserId);
    }

    @Override
    public List<Appointment> getByPatientId(Long patientId, Long currentUserId, String currentRole) {
        if ("PATIENT".equals(currentRole) && !patientId.equals(currentUserId)) {
            throw new UnauthorizedActionException("Patients can only access their own appointments");
        }
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getByDoctorId(Long doctorId, Long currentUserId, String currentRole) {
        if ("DOCTOR".equals(currentRole) && !doctorId.equals(currentUserId)) {
            throw new UnauthorizedActionException("Doctors can only access their own appointments");
        }
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public Appointment update(Long id, AppointmentDTO dto, Long currentUserId, String currentRole) {
        Appointment appointment = getById(id, currentUserId, currentRole);

        if (dto.getAppointmentDate() != null)
            appointment.setAppointmentDate(dto.getAppointmentDate());
        if (dto.getEndTime() != null)
            appointment.setAppointmentEndDate(dto.getEndTime());
        if (dto.getStatus() != null)
            appointment.setStatus(dto.getStatus());
        if (dto.getReason() != null)
            appointment.setReason(dto.getReason());
        if (dto.getConsultationType() != null)
            appointment.setConsultationType(dto.getConsultationType());
        if (dto.getNotes() != null)
            appointment.setNotes(dto.getNotes());

        return appointmentRepository.save(appointment);
    }

    @Override
    public void delete(Long id, Long currentUserId, String currentRole) {
        Appointment appointment = getById(id, currentUserId, currentRole);
        appointmentRepository.delete(appointment);
    }

    @Override
    public List<Appointment> findUpcomingForDoctorKeyword(String doctorKeyword, int windowMinutes) {
        String safeKeyword = doctorKeyword == null ? "" : doctorKeyword.trim();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusMinutes(Math.max(1, windowMinutes));
        return appointmentRepository.findUpcomingForDoctorKeyword(
                safeKeyword,
                List.of("PENDING", "CONFIRMED"),
                now,
                limit
        );
    }

    @Override
    public List<Appointment> searchByKeywords(Long doctorId, String patientKeyword, String reasonKeyword,
                                              Long currentUserId, String currentRole) {
        if ("DOCTOR".equals(currentRole)) {
            doctorId = currentUserId;
        } else if ("PATIENT".equals(currentRole)) {
            return appointmentRepository.findByPatientId(currentUserId);
        }

        return appointmentRepository.searchByKeywords(
                doctorId,
                normalizeKeyword(patientKeyword),
                normalizeKeyword(reasonKeyword)
        );
    }

    @Override
    @Transactional
    public int expirePastPendingAppointments() {
        List<Appointment> expired = appointmentRepository.findByStatusAndAppointmentDateBefore("PENDING", LocalDateTime.now());
        for (Appointment appointment : expired) {
            appointment.setStatus("EXPIRED");
        }
        appointmentRepository.saveAll(expired);
        return expired.size();
    }

    private void ensureCanAccessAppointment(Appointment appointment, Long currentUserId, String currentRole) {
        if ("ADMIN".equals(currentRole)) {
            return;
        }
        if ("DOCTOR".equals(currentRole) && appointment.getDoctor().getId().equals(currentUserId)) {
            return;
        }
        if ("PATIENT".equals(currentRole) && appointment.getPatient().getId().equals(currentUserId)) {
            return;
        }
        throw new UnauthorizedActionException("You are not allowed to access this appointment");
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return keyword.trim();
    }
}

