package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyControllerTest {

    @Mock
    private ReplyService replyService;

    @InjectMocks
    private ReplyController controller;

    @Test
    void createReply_returnsCreated() {
        when(replyService.createReply(any(ReplyRequestDTO.class), org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.eq(2L)))
                .thenReturn(new ReplyResponseDTO());

        ResponseEntity<ReplyResponseDTO> response = controller.createReply(5L, new ReplyRequestDTO(), 2L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getRepliesByPost_returnsOk() {
        when(replyService.getRepliesByPost(5L)).thenReturn(List.of(new ReplyResponseDTO()));

        ResponseEntity<List<ReplyResponseDTO>> response = controller.getRepliesByPost(5L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteReply_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteReply(8L, 2L);

        verify(replyService).deleteReply(8L, 2L);
        assertEquals(204, response.getStatusCode().value());
    }
}

