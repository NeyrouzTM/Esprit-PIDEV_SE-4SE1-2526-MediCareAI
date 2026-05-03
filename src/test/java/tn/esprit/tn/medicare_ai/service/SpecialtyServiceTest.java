package tn.esprit.tn.medicare_ai.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SpecialtyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Specialty;
import tn.esprit.tn.medicare_ai.repository.chatbot.SpecialtyRepository;
import tn.esprit.tn.medicare_ai.service.chatbotImp.SpecialtyImplement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyImplement specialtyService;

    private Specialty specialty;
    private SpecialtyRequestDTO specialtyRequestDTO;
    private SpecialtyResponseDTO specialtyResponseDTO;

    @BeforeEach
    void setUp() {
        specialty = Specialty.builder()
                .id(1L)
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .diseases(new ArrayList<>())
                .build();

        specialtyRequestDTO = SpecialtyRequestDTO.builder()
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .build();

        specialtyResponseDTO = SpecialtyResponseDTO.builder()
                .id(1L)
                .name("Cardiology")
                .description("Heart and cardiovascular system")
                .diseases(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateSpecialty_Success() {
        when(specialtyRepository.existsByName("Cardiology")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);

        SpecialtyResponseDTO result = specialtyService.createSpecialty(specialtyRequestDTO);

        assertNotNull(result);
        assertEquals("Cardiology", result.getName());
        assertEquals("Heart and cardiovascular system", result.getDescription());
        verify(specialtyRepository, times(1)).existsByName("Cardiology");
        verify(specialtyRepository, times(1)).save(any(Specialty.class));
    }

    @Test
    void testCreateSpecialty_AlreadyExists() {
        when(specialtyRepository.existsByName("Cardiology")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> specialtyService.createSpecialty(specialtyRequestDTO));
        verify(specialtyRepository, times(1)).existsByName("Cardiology");
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void testUpdateSpecialty_Success() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);

        SpecialtyResponseDTO result = specialtyService.updateSpecialty(1L, specialtyRequestDTO);

        assertNotNull(result);
        assertEquals("Cardiology", result.getName());
        verify(specialtyRepository, times(1)).findById(1L);
        verify(specialtyRepository, times(1)).save(any(Specialty.class));
    }

    @Test
    void testUpdateSpecialty_NotFound() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specialtyService.updateSpecialty(1L, specialtyRequestDTO));
        verify(specialtyRepository, times(1)).findById(1L);
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void testDeleteSpecialty_Success() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        specialtyService.deleteSpecialty(1L);

        verify(specialtyRepository, times(1)).findById(1L);
        verify(specialtyRepository, times(1)).delete(specialty);
    }

    @Test
    void testDeleteSpecialty_NotFound() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specialtyService.deleteSpecialty(1L));
        verify(specialtyRepository, times(1)).findById(1L);
        verify(specialtyRepository, never()).delete(any());
    }

    @Test
    void testGetSpecialty_Success() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        SpecialtyResponseDTO result = specialtyService.getSpecialty(1L);

        assertNotNull(result);
        assertEquals("Cardiology", result.getName());
        verify(specialtyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSpecialty_NotFound() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specialtyService.getSpecialty(1L));
        verify(specialtyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllSpecialties_Success() {
        List<Specialty> specialties = new ArrayList<>();
        specialties.add(specialty);

        when(specialtyRepository.findAll()).thenReturn(specialties);

        List<SpecialtyResponseDTO> result = specialtyService.getAllSpecialties();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getName());
        verify(specialtyRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSpecialties_Empty() {
        when(specialtyRepository.findAll()).thenReturn(new ArrayList<>());

        List<SpecialtyResponseDTO> result = specialtyService.getAllSpecialties();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(specialtyRepository, times(1)).findAll();
    }
}


