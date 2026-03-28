package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.PregnancyCheckup;
import java.util.List;

@Repository
public interface PregnancyCheckupRepository extends JpaRepository<PregnancyCheckup, Long> {
    List<PregnancyCheckup> findByPregnancyTrackingId(Long pregnancyTrackingId);
}
