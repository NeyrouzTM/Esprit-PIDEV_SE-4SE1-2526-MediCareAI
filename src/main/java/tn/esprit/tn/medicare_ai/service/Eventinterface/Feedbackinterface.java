package tn.esprit.tn.medicare_ai.service.Eventinterface;

import tn.esprit.tn.medicare_ai.entity.Feedback;

import java.util.List;

public interface Feedbackinterface {
    Feedback addFeedback(Feedback feedback);

    List<Feedback> getAllFeedbacks();

    List<Feedback> getFeedbacksByEvent(Long eventId);

    void deleteFeedback(Long id);

    Feedback updateFeedback(Long id, Feedback updatedFeedback); // update
}
