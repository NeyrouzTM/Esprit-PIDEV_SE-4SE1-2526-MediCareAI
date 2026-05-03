package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SymptomRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SymptomResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SymptomInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SymptomControllerTest {

    @Mock
    private SymptomInterface symptomService;

    @InjectMocks
    private SymptomController symptomController;

    private SymptomRequestDTO symptomRequestDTO;
    private SymptomResponseDTO symptomResponseDTO;

    @BeforeEach
    void setUp() {
        symptomRequestDTO = SymptomRequestDTO.builder()
                .name("Chest pain")
                .description("Chest discomfort or tightness")
                .build();

        symptomResponseDTO = SymptomResponseDTO.builder()
                .id(1L)
                .name("Chest pain")
                .description("Chest discomfort or tightness")
                .build();
    }

    @Test
    void testCreateSymptom_Success() {
        when(symptomService.createSymptom(any(SymptomRequestDTO.class)))
                .thenReturn(symptomResponseDTO);

        ResponseEntity<SymptomResponseDTO> response = symptomController.create(symptomRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Chest pain", response.getBody().getName());
        verify(symptomService, times(1)).createSymptom(any(SymptomRequestDTO.class));
    }

    @Test
    void testUpdateSymptom_Success() {
        when(symptomService.updateSymptom(1L, symptomRequestDTO))
                .thenReturn(symptomResponseDTO);

        ResponseEntity<SymptomResponseDTO> response = symptomController.update(1L, symptomRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Chest pain", response.getBody().getName());
        verify(symptomService, times(1)).updateSymptom(1L, symptomRequestDTO);
    }

    @Test
    void testDeleteSymptom_Success() {
        ResponseEntity<Void> response = symptomController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(symptomService, times(1)).deleteSymptom(1L);
    }

    @Test
    void testGetSymptomById_Success() {
        when(symptomService.getSymptom(1L)).thenReturn(symptomResponseDTO);

        ResponseEntity<SymptomResponseDTO> response = symptomController.getById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Chest pain", response.getBody().getName());
        verify(symptomService, times(1)).getSymptom(1L);
    }

    @Test
    void testGetAllSymptoms_Success() {
        List<SymptomResponseDTO> symptoms = new ArrayList<>();
        symptoms.add(symptomResponseDTO);

        when(symptomService.getAllSymptoms()).thenReturn(symptoms);

        ResponseEntity<List<SymptomResponseDTO>> response = symptomController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Chest pain", response.getBody().get(0).getName());
        verify(symptomService, times(1)).getAllSymptoms();
    }

    @Test
    void testGetAllSymptoms_Empty() {
        when(symptomService.getAllSymptoms()).thenReturn(new ArrayList<>());

        ResponseEntity<List<SymptomResponseDTO>> response = symptomController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(symptomService, times(1)).getAllSymptoms();
    }
}

