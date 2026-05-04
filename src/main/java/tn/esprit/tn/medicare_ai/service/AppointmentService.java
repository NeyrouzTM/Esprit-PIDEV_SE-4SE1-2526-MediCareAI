package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment create(AppointmentDTO dto, Long currentUserId, String currentRole);
    Appointment getById(Long id, Long currentUserId, String currentRole);
    List<Appointment> getAll(String currentRole, Long currentUserId);
    List<Appointment> getByPatientId(Long patientId, Long currentUserId, String currentRole);
    List<Appointment> getByDoctorId(Long doctorId, Long currentUserId, String currentRole);
    Appointment update(Long id, AppointmentDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);

    List<Appointment> findUpcomingForDoctorKeyword(String doctorKeyword, int windowMinutes);
    List<Appointment> searchByKeywords(Long doctorId, String patientKeyword, String reasonKeyword,
                                       Long currentUserId, String currentRole);
    int expirePastPendingAppointments();
}