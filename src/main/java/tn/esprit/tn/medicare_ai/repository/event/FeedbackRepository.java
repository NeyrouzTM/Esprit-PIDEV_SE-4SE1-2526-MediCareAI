package tn.esprit.tn.medicare_ai.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByHealthEventId(Long eventId);
}
