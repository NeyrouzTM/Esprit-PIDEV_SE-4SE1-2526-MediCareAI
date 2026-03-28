package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AvailabilityDTO;
import tn.esprit.tn.medicare_ai.entity.Availability;
import java.util.List;

public interface AvailabilityService {
    Availability create(AvailabilityDTO dto);
    Availability getById(Long id);
    List<Availability> getByDoctorId(Long doctorId);
    List<Availability> getAvailableSlots(Long doctorId);
    Availability update(Long id, AvailabilityDTO dto);
    void delete(Long id);
}