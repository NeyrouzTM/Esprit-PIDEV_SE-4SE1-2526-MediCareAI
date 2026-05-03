package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.entity.Feedback;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.repository.event.FeedbackRepository;
import tn.esprit.tn.medicare_ai.service.EventImp.Feedbackmplement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private Feedbackmplement feedbackService;

    private Feedback feedback;
    private HealthEvent healthEvent;

    @BeforeEach
    void setUp() {
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
                .build();
    }

    @Test
    void testAddFeedback_Success() {
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        Feedback result = feedbackService.addFeedback(feedback);

        assertNotNull(result);
        assertEquals("John Doe", result.getUserName());
        assertEquals("Great event!", result.getComment());
        assertEquals(5, result.getRating());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testGetAllFeedbacks_Success() {
        List<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(feedback);

        when(feedbackRepository.findAll()).thenReturn(feedbacks);

        List<Feedback> result = feedbackService.getAllFeedbacks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getUserName());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void testGetAllFeedbacks_Empty() {
        when(feedbackRepository.findAll()).thenReturn(new ArrayList<>());

        List<Feedback> result = feedbackService.getAllFeedbacks();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void testGetFeedbacksByEvent_Success() {
        List<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(feedback);

        when(feedbackRepository.findByHealthEventId(1L)).thenReturn(feedbacks);

        List<Feedback> result = feedbackService.getFeedbacksByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getUserName());
        verify(feedbackRepository, times(1)).findByHealthEventId(1L);
    }

    @Test
    void testGetFeedbacksByEvent_Empty() {
        when(feedbackRepository.findByHealthEventId(1L)).thenReturn(new ArrayList<>());

        List<Feedback> result = feedbackService.getFeedbacksByEvent(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(feedbackRepository, times(1)).findByHealthEventId(1L);
    }

    @Test
    void testDeleteFeedback_Success() {
        feedbackService.deleteFeedback(1L);

        verify(feedbackRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateFeedback_Success() {
        Feedback updatedFeedback = Feedback.builder()
                .userName("Jane Doe")
                .comment("Excellent event!")
                .rating(4)
                .build();

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        Feedback result = feedbackService.updateFeedback(1L, updatedFeedback);

        assertNotNull(result);
        verify(feedbackRepository, times(1)).findById(1L);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testUpdateFeedback_NotFound() {
        Feedback updatedFeedback = Feedback.builder()
                .userName("Jane Doe")
                .comment("Excellent event!")
                .rating(4)
                .build();

        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        Feedback result = feedbackService.updateFeedback(1L, updatedFeedback);

        assertNull(result);
        verify(feedbackRepository, times(1)).findById(1L);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }
}

