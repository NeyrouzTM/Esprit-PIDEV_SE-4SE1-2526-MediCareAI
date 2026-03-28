package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.PregnancyTracking;
import java.util.List;

@Repository
public interface PregnancyTrackingRepository extends JpaRepository<PregnancyTracking, Long> {
    List<PregnancyTracking> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
