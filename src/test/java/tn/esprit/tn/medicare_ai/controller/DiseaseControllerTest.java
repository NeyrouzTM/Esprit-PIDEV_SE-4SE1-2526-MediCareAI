package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.DiseaseInterface;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiseaseControllerTest {

    @Mock
    private DiseaseInterface diseaseService;

    @InjectMocks
    private DiseaseController controller;

    @Test
    void create_returnsOk() {
        when(diseaseService.createDisease(any(DiseaseRequestDTO.class))).thenReturn(new DiseaseResponseDTO());

        ResponseEntity<DiseaseResponseDTO> response = controller.create(new DiseaseRequestDTO());

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAll_returnsOk() {
        when(diseaseService.getAllDiseases()).thenReturn(List.of(new DiseaseResponseDTO()));

        ResponseEntity<List<DiseaseResponseDTO>> response = controller.getAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void delete_callsService() {
        ResponseEntity<Void> response = controller.delete(1L);

        verify(diseaseService).deleteDisease(1L);
        assertEquals(204, response.getStatusCode().value());
    }
}

