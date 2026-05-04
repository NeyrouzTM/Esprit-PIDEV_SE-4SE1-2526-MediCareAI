package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.AvailabilityDTO;
import tn.esprit.tn.medicare_ai.entity.Availability;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Test
    @DisplayName("create: valid dto saves availability as available")
    void create_validDto_savesAvailability() {
        User doctor = new User();
        doctor.setId(6L);

        when(userRepository.findById(6L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.save(any(Availability.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvailabilityDTO dto = AvailabilityDTO.builder()
                .doctorId(6L)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        Availability result = availabilityService.create(dto);

        assertEquals(6L, result.getDoctor().getId());
        assertEquals(true, result.isAvailable());
    }

    @Test
    @DisplayName("create: missing start/end time throws")
    void create_missingTime_throws() {
        AvailabilityDTO dto = AvailabilityDTO.builder()
                .doctorId(6L)
                .date(LocalDate.now().plusDays(1))
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> availabilityService.create(dto));
        assertEquals("Start and end time required", ex.getMessage());
    }
}



