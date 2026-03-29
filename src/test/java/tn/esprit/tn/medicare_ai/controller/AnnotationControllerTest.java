package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.AnnotationRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.AnnotationResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.AnnotationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationControllerTest {

    @Mock
    private AnnotationService annotationService;

    @InjectMocks
    private AnnotationController controller;

    @Test
    void createAnnotation_returnsCreated() {
        when(annotationService.createAnnotation(any(AnnotationRequestDTO.class), eq(11L), eq(2L)))
                .thenReturn(new AnnotationResponseDTO());

        ResponseEntity<AnnotationResponseDTO> response =
                controller.createAnnotation(11L, new AnnotationRequestDTO(), 2L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getAnnotationsByDocument_returnsOk() {
        when(annotationService.getAnnotationsByDocument(11L)).thenReturn(List.of(new AnnotationResponseDTO()));

        ResponseEntity<List<AnnotationResponseDTO>> response = controller.getAnnotationsByDocument(11L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAnnotationById_returnsOk() {
        when(annotationService.getAnnotationById(5L)).thenReturn(new AnnotationResponseDTO());

        ResponseEntity<AnnotationResponseDTO> response = controller.getAnnotationById(5L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateAnnotation_returnsOk() {
        when(annotationService.updateAnnotation(eq(5L), any(AnnotationRequestDTO.class), eq(2L)))
                .thenReturn(new AnnotationResponseDTO());

        ResponseEntity<AnnotationResponseDTO> response =
                controller.updateAnnotation(5L, new AnnotationRequestDTO(), 2L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteAnnotation_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteAnnotation(5L, 2L);

        verify(annotationService).deleteAnnotation(5L, 2L);
        assertEquals(204, response.getStatusCode().value());
    }
}

