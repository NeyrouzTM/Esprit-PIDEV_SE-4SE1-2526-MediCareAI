package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Appointment create(AppointmentDTO dto) {
        if (dto.getPatientId() == null)
            throw new IllegalArgumentException("Patient ID required");
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");
        if (dto.getAppointmentDate() == null)
            throw new IllegalArgumentException("Appointment date required");

        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(dto.getAppointmentDate())
                .status("PENDING")
                .reason(dto.getReason())
                .consultationType(dto.getConsultationType())
                .notes(dto.getNotes())
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public Appointment update(Long id, AppointmentDTO dto) {
        Appointment appointment = getById(id);
        if (dto.getAppointmentDate() != null)
            appointment.setAppointmentDate(dto.getAppointmentDate());
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
    public void delete(Long id) {
        Appointment appointment = getById(id);
        appointmentRepository.delete(appointment);
    }
}