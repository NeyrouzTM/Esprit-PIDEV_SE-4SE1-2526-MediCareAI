package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.SleepRequest;
import tn.esprit.tn.medicare_ai.dto.response.SleepResponse;
import tn.esprit.tn.medicare_ai.service.ISleepService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SleepControllerTest {

    @Mock
    private ISleepService sleepService;

    @InjectMocks
    private SleepController controller;

    @Test
    void create_returnsCreated() {
        when(sleepService.create(any(SleepRequest.class))).thenReturn(new SleepResponse());

        ResponseEntity<SleepResponse> response = controller.create(new SleepRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAll_returnsOk() {
        when(sleepService.getAll()).thenReturn(List.of(new SleepResponse()));

        ResponseEntity<List<SleepResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(sleepService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }
}

