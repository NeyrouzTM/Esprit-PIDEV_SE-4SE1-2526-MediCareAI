package tn.esprit.tn.medicare_ai.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Disease;
import tn.esprit.tn.medicare_ai.entity.Specialty;
import tn.esprit.tn.medicare_ai.entity.Symptom;
import tn.esprit.tn.medicare_ai.repository.chatbot.DiseaseRepository;
import tn.esprit.tn.medicare_ai.repository.chatbot.SpecialtyRepository;
import tn.esprit.tn.medicare_ai.repository.chatbot.SymptomRepository;
import tn.esprit.tn.medicare_ai.service.chatbotImp.DiseaseImplement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiseaseServiceTest {

    @Mock
    private DiseaseRepository diseaseRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SymptomRepository symptomRepository;

    @InjectMocks
    private DiseaseImplement diseaseService;

    private Disease disease;
    private Specialty specialty;
    private Symptom symptom;
    private DiseaseRequestDTO diseaseRequestDTO;

    @BeforeEach
    void setUp() {
        specialty = Specialty.builder()
                .id(1L)
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .build();

        symptom = Symptom.builder()
                .id(1L)
                .name("Chest pain")
                .description("Chest discomfort")
                .build();

        disease = Disease.builder()
                .id(1L)
                .name("Heart Disease")
                .description("Cardiovascular disease")
                .causes("High cholesterol")
                .treatment("Medication and lifestyle changes")
                .specialty(specialty)
                .symptoms(new ArrayList<>())
                .build();

        diseaseRequestDTO = DiseaseRequestDTO.builder()
                .name("Heart Disease")
                .description("Cardiovascular disease")
                .causes("High cholesterol")
                .treatment("Medication and lifestyle changes")
                .specialtyId(1L)
                .symptomIds(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateDisease_Success() {
        when(diseaseRepository.existsByName("Heart Disease")).thenReturn(false);
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(symptomRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        DiseaseResponseDTO result = diseaseService.createDisease(diseaseRequestDTO);

        assertNotNull(result);
        assertEquals("Heart Disease", result.getName());
        verify(diseaseRepository, times(1)).existsByName("Heart Disease");
        verify(specialtyRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    void testCreateDisease_AlreadyExists() {
        when(diseaseRepository.existsByName("Heart Disease")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> diseaseService.createDisease(diseaseRequestDTO));
        verify(diseaseRepository, times(1)).existsByName("Heart Disease");
        verify(diseaseRepository, never()).save(any(Disease.class));
    }

    @Test
    void testCreateDisease_SpecialtyNotFound() {
        when(diseaseRepository.existsByName("Heart Disease")).thenReturn(false);
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diseaseService.createDisease(diseaseRequestDTO));
        verify(diseaseRepository, times(1)).existsByName("Heart Disease");
        verify(diseaseRepository, never()).save(any(Disease.class));
    }

    @Test
    void testUpdateDisease_Success() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(symptomRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        DiseaseResponseDTO result = diseaseService.updateDisease(1L, diseaseRequestDTO);

        assertNotNull(result);
        assertEquals("Heart Disease", result.getName());
        verify(diseaseRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    void testUpdateDisease_NotFound() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diseaseService.updateDisease(1L, diseaseRequestDTO));
        verify(diseaseRepository, times(1)).findById(1L);
        verify(diseaseRepository, never()).save(any(Disease.class));
    }

    @Test
    void testDeleteDisease_Success() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        diseaseService.deleteDisease(1L);

        verify(diseaseRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).delete(disease);
    }

    @Test
    void testDeleteDisease_NotFound() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diseaseService.deleteDisease(1L));
        verify(diseaseRepository, times(1)).findById(1L);
        verify(diseaseRepository, never()).delete(any());
    }

    @Test
    void testGetDisease_Success() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));

        DiseaseResponseDTO result = diseaseService.getDisease(1L);

        assertNotNull(result);
        assertEquals("Heart Disease", result.getName());
        verify(diseaseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDisease_NotFound() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diseaseService.getDisease(1L));
        verify(diseaseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllDiseases_Success() {
        List<Disease> diseases = new ArrayList<>();
        diseases.add(disease);

        when(diseaseRepository.findAll()).thenReturn(diseases);

        List<DiseaseResponseDTO> result = diseaseService.getAllDiseases();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Heart Disease", result.get(0).getName());
        verify(diseaseRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDiseases_Empty() {
        when(diseaseRepository.findAll()).thenReturn(new ArrayList<>());

        List<DiseaseResponseDTO> result = diseaseService.getAllDiseases();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(diseaseRepository, times(1)).findAll();
    }

    @Test
    void testAddSymptom_Success() {
        disease.setSymptoms(new ArrayList<>());
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(symptomRepository.findById(1L)).thenReturn(Optional.of(symptom));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        DiseaseResponseDTO result = diseaseService.addSymptom(1L, 1L);

        assertNotNull(result);
        verify(diseaseRepository, times(1)).findById(1L);
        verify(symptomRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    void testAddSpecialty_Success() {
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        DiseaseResponseDTO result = diseaseService.addSpecialty(1L, 1L);

        assertNotNull(result);
        verify(diseaseRepository, times(1)).findById(1L);
        verify(specialtyRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }

    @Test
    void testRemoveSymptom_Success() {
        disease.setSymptoms(new ArrayList<>(List.of(symptom)));
        when(diseaseRepository.findById(1L)).thenReturn(Optional.of(disease));
        when(diseaseRepository.save(any(Disease.class))).thenReturn(disease);

        DiseaseResponseDTO result = diseaseService.removeSymptom(1L, 1L);

        assertNotNull(result);
        verify(diseaseRepository, times(1)).findById(1L);
        verify(diseaseRepository, times(1)).save(any(Disease.class));
    }
}


