package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.Stress;
import java.util.List;

@Repository
public interface StressRepository extends JpaRepository<Stress, Long> {
    List<Stress> findByUserId(Long userId);
}
