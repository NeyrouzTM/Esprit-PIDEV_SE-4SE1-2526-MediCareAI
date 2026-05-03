package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SpecialtyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SpecialtyInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecialtyControllerTest {

    @Mock
    private SpecialtyInterface specialtyService;

    @InjectMocks
    private SpecialtyController specialtyController;

    private SpecialtyRequestDTO specialtyRequestDTO;
    private SpecialtyResponseDTO specialtyResponseDTO;

    @BeforeEach
    void setUp() {
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
        when(specialtyService.createSpecialty(any(SpecialtyRequestDTO.class)))
                .thenReturn(specialtyResponseDTO);

        ResponseEntity<SpecialtyResponseDTO> response = specialtyController.create(specialtyRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cardiology", response.getBody().getName());
        verify(specialtyService, times(1)).createSpecialty(any(SpecialtyRequestDTO.class));
    }

    @Test
    void testUpdateSpecialty_Success() {
        when(specialtyService.updateSpecialty(1L, specialtyRequestDTO))
                .thenReturn(specialtyResponseDTO);

        ResponseEntity<SpecialtyResponseDTO> response = specialtyController.update(1L, specialtyRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cardiology", response.getBody().getName());
        verify(specialtyService, times(1)).updateSpecialty(1L, specialtyRequestDTO);
    }

    @Test
    void testDeleteSpecialty_Success() {
        ResponseEntity<Void> response = specialtyController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(specialtyService, times(1)).deleteSpecialty(1L);
    }

    @Test
    void testGetSpecialtyById_Success() {
        when(specialtyService.getSpecialty(1L)).thenReturn(specialtyResponseDTO);

        ResponseEntity<SpecialtyResponseDTO> response = specialtyController.getById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cardiology", response.getBody().getName());
        verify(specialtyService, times(1)).getSpecialty(1L);
    }

    @Test
    void testGetAllSpecialties_Success() {
        List<SpecialtyResponseDTO> specialties = new ArrayList<>();
        specialties.add(specialtyResponseDTO);

        when(specialtyService.getAllSpecialties()).thenReturn(specialties);

        ResponseEntity<List<SpecialtyResponseDTO>> response = specialtyController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Cardiology", response.getBody().get(0).getName());
        verify(specialtyService, times(1)).getAllSpecialties();
    }

    @Test
    void testGetAllSpecialties_Empty() {
        when(specialtyService.getAllSpecialties()).thenReturn(new ArrayList<>());

        ResponseEntity<List<SpecialtyResponseDTO>> response = specialtyController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(specialtyService, times(1)).getAllSpecialties();
    }
}

