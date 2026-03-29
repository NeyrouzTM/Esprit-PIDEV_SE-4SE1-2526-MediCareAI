package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.MedicationReminderRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationReminderResponse;
import tn.esprit.tn.medicare_ai.service.IMedicationReminderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationReminderControllerTest {

    @Mock
    private IMedicationReminderService reminderService;

    @InjectMocks
    private MedicationReminderController controller;

    @Test
    void create_returnsCreated() {
        when(reminderService.create(any(MedicationReminderRequest.class))).thenReturn(new MedicationReminderResponse());

        ResponseEntity<MedicationReminderResponse> response = controller.create(new MedicationReminderRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAll_returnsOk() {
        when(reminderService.getAll()).thenReturn(List.of(new MedicationReminderResponse()));

        ResponseEntity<List<MedicationReminderResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(reminderService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }
}

