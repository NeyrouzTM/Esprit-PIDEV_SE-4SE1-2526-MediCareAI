package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.MedicationScheduleRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationScheduleResponse;
import tn.esprit.tn.medicare_ai.service.IMedicationScheduleService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationScheduleControllerTest {

    @Mock
    private IMedicationScheduleService scheduleService;

    @InjectMocks
    private MedicationScheduleController controller;

    @Test
    void create_returnsCreated() {
        when(scheduleService.create(any(MedicationScheduleRequest.class))).thenReturn(new MedicationScheduleResponse());

        ResponseEntity<MedicationScheduleResponse> response = controller.create(new MedicationScheduleRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAll_returnsOk() {
        when(scheduleService.getAll()).thenReturn(List.of(new MedicationScheduleResponse()));

        ResponseEntity<List<MedicationScheduleResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(scheduleService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }
}
