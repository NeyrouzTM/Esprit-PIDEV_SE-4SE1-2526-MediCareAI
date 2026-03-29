package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.MoodRequest;
import tn.esprit.tn.medicare_ai.dto.response.MoodResponse;
import tn.esprit.tn.medicare_ai.service.IMoodService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoodControllerTest {

    @Mock
    private IMoodService moodService;

    @InjectMocks
    private MoodController controller;

    @Test
    void create_returnsCreated() {
        when(moodService.create(any(MoodRequest.class))).thenReturn(new MoodResponse());

        ResponseEntity<MoodResponse> response = controller.create(new MoodRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAll_returnsOk() {
        when(moodService.getAll()).thenReturn(List.of(new MoodResponse()));

        ResponseEntity<List<MoodResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(moodService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }
}

