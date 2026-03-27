package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import java.util.List;

public interface AppointmentService {
    Appointment create(AppointmentDTO dto);
    Appointment getById(Long id);
    List<Appointment> getAll();
    List<Appointment> getByPatientId(Long patientId);
    List<Appointment> getByDoctorId(Long doctorId);
    Appointment update(Long id, AppointmentDTO dto);
    void delete(Long id);
}