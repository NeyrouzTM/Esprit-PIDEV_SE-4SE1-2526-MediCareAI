package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Meeting;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MeetingRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.MeetingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Test
    @DisplayName("createMeeting: valid request creates meeting")
    void createMeeting_validRequest_createsMeeting() {
        User organizer = new User();
        organizer.setId(1L);
        organizer.setFullName("Test Organizer");
        organizer.setRole(Role.DOCTOR);

        MeetingRequestDTO request = MeetingRequestDTO.builder()
                .title("Test Meeting")
                .dateTime(LocalDateTime.now().plusDays(1))
                .meetingLink("https://meet.example.com/test")
                .recorded(false)
                .build();

        Meeting savedMeeting = Meeting.builder()
                .id(1L)
                .title("Test Meeting")
                .dateTime(request.getDateTime())
                .meetingLink("https://meet.example.com/test")
                .organizer(organizer)
                .recorded(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(savedMeeting);

        MeetingResponseDTO result = meetingService.createMeeting(request, 1L);

        assertEquals("Test Meeting", result.getTitle());
        assertEquals(1L, result.getOrganizerId());
        verify(meetingRepository).save(any(Meeting.class));
    }

    @Test
    @DisplayName("getAllMeetings: returns all meetings")
    void getAllMeetings_returnsAllMeetings() {
        User organizer = new User();
        organizer.setId(1L);
        organizer.setFullName("Test Organizer");

        Meeting meeting = Meeting.builder()
                .id(1L)
                .title("Test Meeting")
                .dateTime(LocalDateTime.now().plusDays(1))
                .meetingLink("https://meet.example.com/test")
                .organizer(organizer)
                .recorded(false)
                .build();

        when(meetingRepository.findAll()).thenReturn(List.of(meeting));

        List<MeetingResponseDTO> result = meetingService.getAllMeetings();

        assertEquals(1, result.size());
        assertEquals("Test Meeting", result.get(0).getTitle());
    }

    @Test
    @DisplayName("getMeetingById: valid id returns meeting")
    void getMeetingById_validId_returnsMeeting() {
        User organizer = new User();
        organizer.setId(1L);
        organizer.setFullName("Test Organizer");

        Meeting meeting = Meeting.builder()
                .id(1L)
                .title("Test Meeting")
                .dateTime(LocalDateTime.now().plusDays(1))
                .meetingLink("https://meet.example.com/test")
                .organizer(organizer)
                .recorded(false)
                .build();

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        MeetingResponseDTO result = meetingService.getMeetingById(1L);

        assertEquals("Test Meeting", result.getTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getMeetingById: invalid id throws exception")
    void getMeetingById_invalidId_throwsException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> meetingService.getMeetingById(1L));
    }
}



