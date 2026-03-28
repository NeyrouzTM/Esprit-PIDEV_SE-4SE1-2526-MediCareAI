package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.Activity;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByPregnancyTrackingId(Long pregnancyTrackingId);
}
