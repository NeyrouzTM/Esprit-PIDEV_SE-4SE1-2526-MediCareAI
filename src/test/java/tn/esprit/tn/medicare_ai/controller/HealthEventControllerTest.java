package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO.HealthEventRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.HealthEventResponseDTO;
import tn.esprit.tn.medicare_ai.entity.EventCategory;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.HealthEventinterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthEventControllerTest {

    @Mock private HealthEventRepository healthEventRepository;
    @Mock private HealthEventinterface healthEventService;

    @InjectMocks
    private HealthEventController controller;

    @Test
    void add_returnsMappedDto() {
        HealthEventRequestDTO request = new HealthEventRequestDTO();
        request.setTitle("Camp");
        request.setCategory("AWARENESS");
        request.setEventDate(LocalDateTime.now().plusDays(1));

        HealthEvent saved = new HealthEvent();
        saved.setId(5L);
        saved.setTitle("Camp");
        saved.setCategory(EventCategory.AWARENESS);

        when(healthEventService.addEvent(any(HealthEvent.class))).thenReturn(saved);

        HealthEventResponseDTO response = controller.add(request);

        assertNotNull(response);
        assertEquals(5L, response.getId());
    }

    @Test
    void getAll_returnsList() {
        HealthEvent event = new HealthEvent();
        event.setId(2L);
        event.setTitle("Day");
        event.setCategory(EventCategory.AWARENESS);

        when(healthEventService.getAllEvents()).thenReturn(List.of(event));

        List<HealthEventResponseDTO> response = controller.getAll();

        assertEquals(1, response.size());
        assertEquals(2L, response.get(0).getId());
    }

    @Test
    void deleteEvent_whenFound_deletesEntity() {
        HealthEvent event = new HealthEvent();
        event.setParticipants(new ArrayList<>());
        when(healthEventRepository.findById(8L)).thenReturn(Optional.of(event));

        controller.deleteEvent(8L);

        verify(healthEventRepository).delete(event);
    }
}
