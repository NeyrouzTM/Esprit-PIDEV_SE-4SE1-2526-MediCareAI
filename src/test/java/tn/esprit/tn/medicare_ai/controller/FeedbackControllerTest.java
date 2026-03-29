package tn.esprit.tn.medicare_ai.controller;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock private Feedbackinterface feedbackService;
    @Mock private HealthEventRepository healthEventRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private FeedbackController controller;

    @Test
    void add_returnsMappedDto() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setUserName("Ahmed");
        request.setComment("Great");
        request.setRating(5);
        request.setHealthEventId(1L);
        request.setUserId(2L);

        HealthEvent event = new HealthEvent();
        event.setId(1L);
        User user = new User();
        user.setId(2L);

        Feedback saved = new Feedback();
        saved.setId(10L);
        saved.setUserName("Ahmed");
        saved.setComment("Great");
        saved.setRating(5);
        saved.setHealthEvent(event);
        saved.setUser(user);

        when(healthEventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(feedbackService.addFeedback(any(Feedback.class))).thenReturn(saved);

        FeedbackResponseDTO response = controller.add(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(2L, response.getUserId());
    }

    @Test
    void getAll_returnsList() {
        Feedback feedback = new Feedback();
        feedback.setId(3L);
        feedback.setUserName("Sara");

        when(feedbackService.getAllFeedbacks()).thenReturn(List.of(feedback));

        List<FeedbackResponseDTO> response = controller.getAll();

        assertEquals(1, response.size());
        assertEquals(3L, response.get(0).getId());
    }

    @Test
    void delete_callsService() {
        controller.delete(7L);

        verify(feedbackService).deleteFeedback(7L);
    }
}

