package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.WellBeingMetric;
import java.util.List;

@Repository
public interface WellBeingMetricRepository extends JpaRepository<WellBeingMetric, Long> {
    List<WellBeingMetric> findByUserId(Long userId);
}
