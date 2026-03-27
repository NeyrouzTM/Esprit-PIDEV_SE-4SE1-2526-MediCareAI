package tn.esprit.tn.medicare_ai.service.EventImp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.entity.Feedback;
import tn.esprit.tn.medicare_ai.repository.event.FeedbackRepository;
import tn.esprit.tn.medicare_ai.service.Eventinterface.Feedbackinterface;

import java.util.List;

@Service
@RequiredArgsConstructor

public class Feedbackmplement implements Feedbackinterface {
    private final FeedbackRepository feedbackRepository;

    @Override
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<Feedback> getFeedbacksByEvent(Long eventId) {
        return feedbackRepository.findByHealthEventId(eventId);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
    @Override
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        return feedbackRepository.findById(id).map(f -> {
            f.setUserName(updatedFeedback.getUserName());
            f.setComment(updatedFeedback.getComment());
            f.setRating(updatedFeedback.getRating());
            return feedbackRepository.save(f);
        }).orElse(null);
    }
}
