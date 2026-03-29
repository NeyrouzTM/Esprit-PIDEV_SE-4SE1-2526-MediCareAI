package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingControllerTest {

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController controller;

    @Test
    void createMeeting_returnsCreated() {
        when(meetingService.createMeeting(any(MeetingRequestDTO.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(new MeetingResponseDTO());

        ResponseEntity<MeetingResponseDTO> response = controller.createMeeting(new MeetingRequestDTO(), 1L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getAll_returnsOk() {
        when(meetingService.getAllMeetings()).thenReturn(List.of(new MeetingResponseDTO()));

        ResponseEntity<List<MeetingResponseDTO>> response = controller.getAllMeetings();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteMeeting_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteMeeting(3L, 1L);

        verify(meetingService).deleteMeeting(3L, 1L);
        assertEquals(204, response.getStatusCode().value());
    }
}

