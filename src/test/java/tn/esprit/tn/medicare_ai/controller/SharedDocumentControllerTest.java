package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.SharedDocumentRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SharedDocumentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SharedDocumentControllerTest {

    @Mock
    private SharedDocumentService documentService;

    @InjectMocks
    private SharedDocumentController controller;

    @Test
    void uploadDocument_returnsCreated() {
        when(documentService.uploadDocument(any(SharedDocumentRequestDTO.class), org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(2L)))
                .thenReturn(new SharedDocumentResponseDTO());

        ResponseEntity<SharedDocumentResponseDTO> response =
                controller.uploadDocument(1L, new SharedDocumentRequestDTO(), 2L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getBySession_returnsOk() {
        when(documentService.getDocumentsBySession(1L)).thenReturn(List.of(new SharedDocumentResponseDTO()));

        ResponseEntity<List<SharedDocumentResponseDTO>> response = controller.getDocumentsBySession(1L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteDocument_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteDocument(9L, 2L);

        verify(documentService).deleteDocument(9L, 2L);
        assertEquals(204, response.getStatusCode().value());
    }
}

