package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.CollaborationSessionRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.CollaborationSessionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollaborationSessionControllerTest {

    @Mock
    private CollaborationSessionService sessionService;

    @InjectMocks
    private CollaborationSessionController controller;

    @Test
    void createSession_returnsCreated() {
        when(sessionService.createSession(any(CollaborationSessionRequestDTO.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(new CollaborationSessionResponseDTO());

        ResponseEntity<CollaborationSessionResponseDTO> response =
                controller.createSession(new CollaborationSessionRequestDTO(), 1L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getAll_returnsOk() {
        when(sessionService.getAllSessions()).thenReturn(List.of(new CollaborationSessionResponseDTO()));

        ResponseEntity<List<CollaborationSessionResponseDTO>> response = controller.getAllSessions();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteSession_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteSession(9L, 1L);

        verify(sessionService).deleteSession(9L, 1L);
        assertEquals(204, response.getStatusCode().value());
    }
}

