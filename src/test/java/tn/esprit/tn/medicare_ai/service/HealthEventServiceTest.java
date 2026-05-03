package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.entity.EventCategory;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.EventImp.HealthEventimplement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthEventServiceTest {

    @Mock
    private HealthEventRepository healthEventRepository;

    @InjectMocks
    private HealthEventimplement healthEventService;

    private HealthEvent healthEvent;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        healthEvent = HealthEvent.builder()
                .id(1L)
                .title("Health Awareness Campaign")
                .category(EventCategory.AWARENESS)
                .description("An event to raise health awareness")
                .eventDate(LocalDateTime.now().plusDays(7))
                .location("Hospital")
                .participants(new ArrayList<>())
                .build();
    }

    @Test
    void testAddEvent_Success() {
        when(healthEventRepository.save(any(HealthEvent.class))).thenReturn(healthEvent);

        HealthEvent result = healthEventService.addEvent(healthEvent);

        assertNotNull(result);
        assertEquals("Health Awareness Campaign", result.getTitle());
        assertEquals("Hospital", result.getLocation());
        verify(healthEventRepository, times(1)).save(any(HealthEvent.class));
    }

    @Test
    void testGetAllEvents_Success() {
        List<HealthEvent> events = new ArrayList<>();
        events.add(healthEvent);

        when(healthEventRepository.findAll()).thenReturn(events);

        List<HealthEvent> result = healthEventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Health Awareness Campaign", result.get(0).getTitle());
        verify(healthEventRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEvents_Empty() {
        when(healthEventRepository.findAll()).thenReturn(new ArrayList<>());

        List<HealthEvent> result = healthEventService.getAllEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(healthEventRepository, times(1)).findAll();
    }

    @Test
    void testGetEventById_Success() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));

        HealthEvent result = healthEventService.getEventById(1L);

        assertNotNull(result);
        assertEquals("Health Awareness Campaign", result.getTitle());
        verify(healthEventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.empty());

        HealthEvent result = healthEventService.getEventById(1L);

        assertNull(result);
        verify(healthEventRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteEvent_Success() {
        healthEventService.deleteEvent(1L);

        verify(healthEventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateEvent_Success() {
        HealthEvent updatedEvent = HealthEvent.builder()
                .title("Updated Event Title")
                .category(EventCategory.VACCINATION)
                .description("Updated description")
                .eventDate(LocalDateTime.now().plusDays(14))
                .location("Community Center")
                .build();

        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));
        when(healthEventRepository.save(any(HealthEvent.class))).thenReturn(healthEvent);

        HealthEvent result = healthEventService.updateEvent(1L, updatedEvent);

        assertNotNull(result);
        assertEquals("Updated Event Title", result.getTitle());
        verify(healthEventRepository, times(1)).findById(1L);
        verify(healthEventRepository, times(1)).save(any(HealthEvent.class));
    }

    @Test
    void testUpdateEvent_NotFound() {
        HealthEvent updatedEvent = HealthEvent.builder()
                .title("Updated Event")
                .build();

        when(healthEventRepository.findById(1L)).thenReturn(Optional.empty());

        HealthEvent result = healthEventService.updateEvent(1L, updatedEvent);

        assertNull(result);
        verify(healthEventRepository, times(1)).findById(1L);
        verify(healthEventRepository, never()).save(any(HealthEvent.class));
    }

    @Test
    void testAddParticipant_Success() {
        healthEvent.setParticipants(new ArrayList<>());

        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));
        when(healthEventRepository.save(any(HealthEvent.class))).thenReturn(healthEvent);

        HealthEvent result = healthEventService.addParticipant(1L, user);

        assertNotNull(result);
        verify(healthEventRepository, times(1)).findById(1L);
        verify(healthEventRepository, times(1)).save(any(HealthEvent.class));
    }

    @Test
    void testAddParticipant_EventNotFound() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.empty());

        HealthEvent result = healthEventService.addParticipant(1L, user);

        assertNull(result);
        verify(healthEventRepository, times(1)).findById(1L);
        verify(healthEventRepository, never()).save(any(HealthEvent.class));
    }
}


