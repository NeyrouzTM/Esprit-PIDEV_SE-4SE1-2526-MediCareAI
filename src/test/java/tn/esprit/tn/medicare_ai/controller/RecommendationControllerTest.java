package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.RecommendationRequest;
import tn.esprit.tn.medicare_ai.dto.response.RecommendationResponse;
import tn.esprit.tn.medicare_ai.service.IRecommendationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private IRecommendationService recommendationService;

    @InjectMocks
    private RecommendationController controller;

    @Test
    void create_returnsCreated() {
        when(recommendationService.create(any(RecommendationRequest.class))).thenReturn(new RecommendationResponse());

        ResponseEntity<RecommendationResponse> response = controller.create(new RecommendationRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAll_returnsOk() {
        when(recommendationService.getAll()).thenReturn(List.of(new RecommendationResponse()));

        ResponseEntity<List<RecommendationResponse>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(recommendationService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }
}

