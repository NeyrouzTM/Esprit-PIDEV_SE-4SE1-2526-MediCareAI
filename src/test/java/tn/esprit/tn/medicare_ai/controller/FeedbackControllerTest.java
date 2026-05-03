package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO.FeedbackRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.FeedbackResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Feedback;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.Feedbackinterface;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {

    @Mock
    private Feedbackinterface feedbackService;

    @Mock
    private HealthEventRepository healthEventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackController feedbackController;

    private FeedbackRequestDTO feedbackRequestDTO;
    private Feedback feedback;
    private HealthEvent healthEvent;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        healthEvent = HealthEvent.builder()
                .id(1L)
                .title("Health Event")
                .build();

        feedback = Feedback.builder()
                .id(1L)
                .userName("John Doe")
                .comment("Great event!")
                .rating(5)
                .healthEvent(healthEvent)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRequestDTO = new FeedbackRequestDTO();
        feedbackRequestDTO.setUserName("John Doe");
        feedbackRequestDTO.setComment("Great event!");
        feedbackRequestDTO.setRating(5);
        feedbackRequestDTO.setHealthEventId(1L);
        feedbackRequestDTO.setUserId(1L);
    }

    @Test
    void testAddFeedback_Success() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(feedbackService.addFeedback(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponseDTO result = feedbackController.add(feedbackRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getUserName());
        assertEquals("Great event!", result.getComment());
        assertEquals(5, result.getRating());
        verify(healthEventRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(feedbackService, times(1)).addFeedback(any(Feedback.class));
    }

    @Test
    void testAddFeedback_EventNotFound() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(feedbackService.addFeedback(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponseDTO result = feedbackController.add(feedbackRequestDTO);

        assertNotNull(result);
        verify(feedbackService, times(1)).addFeedback(any(Feedback.class));
    }

    @Test
    void testAddFeedback_UserNotFound() {
        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(healthEvent));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(feedbackService.addFeedback(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponseDTO result = feedbackController.add(feedbackRequestDTO);

        assertNotNull(result);
        verify(feedbackService, times(1)).addFeedback(any(Feedback.class));
    }
}


