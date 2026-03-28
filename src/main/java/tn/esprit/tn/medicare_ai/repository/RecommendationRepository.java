package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.Recommendation;
import tn.esprit.tn.medicare_ai.entity.RecommendationCategory;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByPregnancyTrackingId(Long pregnancyTrackingId);
    List<Recommendation> findByPregnancyTrackingIdAndCategory(Long pregnancyTrackingId, RecommendationCategory category);
}
