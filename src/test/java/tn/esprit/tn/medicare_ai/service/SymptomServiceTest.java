package tn.esprit.tn.medicare_ai.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SymptomRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SymptomResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Symptom;
import tn.esprit.tn.medicare_ai.repository.chatbot.SymptomRepository;
import tn.esprit.tn.medicare_ai.service.chatbotImp.SymptomImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SymptomServiceTest {

    @Mock
    private SymptomRepository symptomRepository;

    @InjectMocks
    private SymptomImp symptomService;

    private Symptom symptom;
    private SymptomRequestDTO symptomRequestDTO;
    private SymptomResponseDTO symptomResponseDTO;

    @BeforeEach
    void setUp() {
        symptom = Symptom.builder()
                .id(1L)
                .name("Chest pain")
                .description("Chest discomfort or tightness")
                .build();

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
        when(symptomRepository.existsByName("Chest pain")).thenReturn(false);
        when(symptomRepository.save(any(Symptom.class))).thenReturn(symptom);

        SymptomResponseDTO result = symptomService.createSymptom(symptomRequestDTO);

        assertNotNull(result);
        assertEquals("Chest pain", result.getName());
        assertEquals("Chest discomfort or tightness", result.getDescription());
        verify(symptomRepository, times(1)).existsByName("Chest pain");
        verify(symptomRepository, times(1)).save(any(Symptom.class));
    }

    @Test
    void testCreateSymptom_AlreadyExists() {
        when(symptomRepository.existsByName("Chest pain")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> symptomService.createSymptom(symptomRequestDTO));
        verify(symptomRepository, times(1)).existsByName("Chest pain");
        verify(symptomRepository, never()).save(any(Symptom.class));
    }

    @Test
    void testUpdateSymptom_Success() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.of(symptom));
        when(symptomRepository.save(any(Symptom.class))).thenReturn(symptom);

        SymptomResponseDTO result = symptomService.updateSymptom(1L, symptomRequestDTO);

        assertNotNull(result);
        assertEquals("Chest pain", result.getName());
        verify(symptomRepository, times(1)).findById(1L);
        verify(symptomRepository, times(1)).save(any(Symptom.class));
    }

    @Test
    void testUpdateSymptom_NotFound() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> symptomService.updateSymptom(1L, symptomRequestDTO));
        verify(symptomRepository, times(1)).findById(1L);
        verify(symptomRepository, never()).save(any(Symptom.class));
    }

    @Test
    void testDeleteSymptom_Success() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.of(symptom));

        symptomService.deleteSymptom(1L);

        verify(symptomRepository, times(1)).findById(1L);
        verify(symptomRepository, times(1)).delete(symptom);
    }

    @Test
    void testDeleteSymptom_NotFound() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> symptomService.deleteSymptom(1L));
        verify(symptomRepository, times(1)).findById(1L);
        verify(symptomRepository, never()).delete(any());
    }

    @Test
    void testGetSymptom_Success() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.of(symptom));

        SymptomResponseDTO result = symptomService.getSymptom(1L);

        assertNotNull(result);
        assertEquals("Chest pain", result.getName());
        verify(symptomRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSymptom_NotFound() {
        when(symptomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> symptomService.getSymptom(1L));
        verify(symptomRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllSymptoms_Success() {
        List<Symptom> symptoms = new ArrayList<>();
        symptoms.add(symptom);

        when(symptomRepository.findAll()).thenReturn(symptoms);

        List<SymptomResponseDTO> result = symptomService.getAllSymptoms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chest pain", result.get(0).getName());
        verify(symptomRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSymptoms_Empty() {
        when(symptomRepository.findAll()).thenReturn(new ArrayList<>());

        List<SymptomResponseDTO> result = symptomService.getAllSymptoms();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(symptomRepository, times(1)).findAll();
    }
}


