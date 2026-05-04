package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.AvailabilityDTO;
import tn.esprit.tn.medicare_ai.entity.Availability;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Override
    public Availability create(AvailabilityDTO dto) {
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");
        if (dto.getDate() == null)
            throw new IllegalArgumentException("Date required");
        if (dto.getStartTime() == null || dto.getEndTime() == null)
            throw new IllegalArgumentException("Start and end time required");

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        Availability availability = Availability.builder()
                .doctor(doctor)
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .available(true)
                .build();

        return availabilityRepository.save(availability);
    }

    @Override
    public Availability getById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Availability not found"));
    }

    @Override
    public List<Availability> getByDoctorId(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Availability> getAvailableSlots(Long doctorId) {
        return availabilityRepository.findByDoctor_IdAndAvailable(doctorId, true);
    }

    @Override
    public Availability update(Long id, AvailabilityDTO dto) {
        Availability availability = getById(id);
        if (dto.getDate() != null)
            availability.setDate(dto.getDate());
        if (dto.getStartTime() != null)
            availability.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null)
            availability.setEndTime(dto.getEndTime());
        availability.setAvailable(dto.isAvailable());
        return availabilityRepository.save(availability);
    }

    @Override
    public void delete(Long id) {
        Availability availability = getById(id);
        availabilityRepository.delete(availability);
    }
}