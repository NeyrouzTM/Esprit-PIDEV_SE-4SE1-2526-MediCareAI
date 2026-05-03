package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.DiseaseInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiseaseControllerTest {

    @Mock
    private DiseaseInterface diseaseService;

    @InjectMocks
    private DiseaseController diseaseController;

    private DiseaseRequestDTO diseaseRequestDTO;
    private DiseaseResponseDTO diseaseResponseDTO;

    @BeforeEach
    void setUp() {
        diseaseRequestDTO = DiseaseRequestDTO.builder()
                .name("Heart Disease")
                .description("Cardiovascular disease")
                .causes("High cholesterol")
                .treatment("Medication and lifestyle changes")
                .specialtyId(1L)
                .symptomIds(new ArrayList<>())
                .build();

        diseaseResponseDTO = DiseaseResponseDTO.builder()
                .id(1L)
                .name("Heart Disease")
                .description("Cardiovascular disease")
                .causes("High cholesterol")
                .treatment("Medication and lifestyle changes")
                .specialtyName("Cardiology")
                .symptoms(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateDisease_Success() {
        when(diseaseService.createDisease(any(DiseaseRequestDTO.class)))
                .thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.create(diseaseRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Heart Disease", response.getBody().getName());
        verify(diseaseService, times(1)).createDisease(any(DiseaseRequestDTO.class));
    }

    @Test
    void testUpdateDisease_Success() {
        when(diseaseService.updateDisease(1L, diseaseRequestDTO))
                .thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.update(1L, diseaseRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Heart Disease", response.getBody().getName());
        verify(diseaseService, times(1)).updateDisease(1L, diseaseRequestDTO);
    }

    @Test
    void testDeleteDisease_Success() {
        ResponseEntity<Void> response = diseaseController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(diseaseService, times(1)).deleteDisease(1L);
    }

    @Test
    void testGetDiseaseById_Success() {
        when(diseaseService.getDisease(1L)).thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.getById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Heart Disease", response.getBody().getName());
        verify(diseaseService, times(1)).getDisease(1L);
    }

    @Test
    void testGetAllDiseases_Success() {
        List<DiseaseResponseDTO> diseases = new ArrayList<>();
        diseases.add(diseaseResponseDTO);

        when(diseaseService.getAllDiseases()).thenReturn(diseases);

        ResponseEntity<List<DiseaseResponseDTO>> response = diseaseController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Heart Disease", response.getBody().get(0).getName());
        verify(diseaseService, times(1)).getAllDiseases();
    }

    @Test
    void testGetAllDiseases_Empty() {
        when(diseaseService.getAllDiseases()).thenReturn(new ArrayList<>());

        ResponseEntity<List<DiseaseResponseDTO>> response = diseaseController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(diseaseService, times(1)).getAllDiseases();
    }

    @Test
    void testAddSpecialty_Success() {
        when(diseaseService.addSpecialty(1L, 1L)).thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.addSpecialty(1L, 1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(diseaseService, times(1)).addSpecialty(1L, 1L);
    }

    @Test
    void testAddSymptom_Success() {
        when(diseaseService.addSymptom(1L, 1L)).thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.addSymptom(1L, 1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(diseaseService, times(1)).addSymptom(1L, 1L);
    }

    @Test
    void testRemoveSymptom_Success() {
        when(diseaseService.removeSymptom(1L, 1L)).thenReturn(diseaseResponseDTO);

        ResponseEntity<DiseaseResponseDTO> response = diseaseController.removeSymptom(1L, 1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(diseaseService, times(1)).removeSymptom(1L, 1L);
    }
}

