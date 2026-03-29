package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ActivityRequest;
import tn.esprit.tn.medicare_ai.dto.response.ActivityResponse;
import tn.esprit.tn.medicare_ai.service.IActivityService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private IActivityService service;

    @InjectMocks
    private ActivityController controller;

    @Test
    void create_returnsCreated() {
        when(service.create(any(ActivityRequest.class))).thenReturn(new ActivityResponse());

        ResponseEntity<ActivityResponse> response = controller.create(new ActivityRequest());

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getById_returnsOk() {
        when(service.getById(1L)).thenReturn(new ActivityResponse());

        ResponseEntity<ActivityResponse> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAll_returnsOk() {
        when(service.getAll()).thenReturn(List.of(new ActivityResponse()));

        ResponseEntity<List<ActivityResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void update_returnsOk() {
        when(service.update(eq(1L), any(ActivityRequest.class))).thenReturn(new ActivityResponse());

        ResponseEntity<ActivityResponse> response = controller.update(1L, new ActivityRequest());

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(1L);

        verify(service).delete(1L);
        assertEquals(204, response.getStatusCode().value());
    }
}

