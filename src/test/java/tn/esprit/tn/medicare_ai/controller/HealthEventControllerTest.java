package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO.HealthEventRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.HealthEventResponseDTO;
import tn.esprit.tn.medicare_ai.entity.EventCategory;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.HealthEventinterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthEventControllerTest {

    @Mock
    private HealthEventRepository healthEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HealthEventinterface healthEventService;

    @InjectMocks
    private HealthEventController healthEventController;

    private HealthEventRequestDTO healthEventRequestDTO;
    private HealthEvent healthEvent;
    private HealthEventResponseDTO healthEventResponseDTO;

    @BeforeEach
    void setUp() {
        healthEventRequestDTO = new HealthEventRequestDTO();
        healthEventRequestDTO.setTitle("Health Awareness Campaign");
        healthEventRequestDTO.setCategory("AWARENESS");
        healthEventRequestDTO.setDescription("An event to raise health awareness");
        healthEventRequestDTO.setEventDate(LocalDateTime.now().plusDays(7));
        healthEventRequestDTO.setLocation("Hospital");

        healthEvent = HealthEvent.builder()
                .id(1L)
                .title("Health Awareness Campaign")
                .category(EventCategory.AWARENESS)
                .description("An event to raise health awareness")
                .eventDate(LocalDateTime.now().plusDays(7))
                .location("Hospital")
                .participants(new ArrayList<>())
                .build();

        healthEventResponseDTO = new HealthEventResponseDTO();
        healthEventResponseDTO.setId(1L);
        healthEventResponseDTO.setTitle("Health Awareness Campaign");
        healthEventResponseDTO.setCategory("AWARENESS");
        healthEventResponseDTO.setDescription("An event to raise health awareness");
        healthEventResponseDTO.setEventDate(LocalDateTime.now().plusDays(7));
        healthEventResponseDTO.setLocation("Hospital");
    }

    @Test
    void testAddEvent_Success() {
        when(healthEventService.addEvent(any(HealthEvent.class))).thenReturn(healthEvent);

        HealthEventResponseDTO result = healthEventController.add(healthEventRequestDTO);

        assertNotNull(result);
        assertEquals("Health Awareness Campaign", result.getTitle());
        assertEquals("Hospital", result.getLocation());
        verify(healthEventService, times(1)).addEvent(any(HealthEvent.class));
    }

    @Test
    void testGetAllEvents_Success() {
        List<HealthEvent> events = new ArrayList<>();
        events.add(healthEvent);

        when(healthEventService.getAllEvents()).thenReturn(events);

        List<HealthEventResponseDTO> result = healthEventController.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Health Awareness Campaign", result.get(0).getTitle());
        verify(healthEventService, times(1)).getAllEvents();
    }

    @Test
    void testGetAllEvents_Empty() {
        when(healthEventService.getAllEvents()).thenReturn(new ArrayList<>());

        List<HealthEventResponseDTO> result = healthEventController.getAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(healthEventService, times(1)).getAllEvents();
    }

    @Test
    void testDeleteEvent_Success() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));

        healthEventController.deleteEvent(1L);

        verify(healthEventRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteEvent_NotFound() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.empty());

        healthEventController.deleteEvent(1L);

        verify(healthEventRepository, times(1)).findById(1L);
    }
}


