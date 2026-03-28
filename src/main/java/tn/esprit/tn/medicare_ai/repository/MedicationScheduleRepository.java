package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.MedicationSchedule;
import java.util.List;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long> {
    List<MedicationSchedule> findByUserId(Long userId);
}
