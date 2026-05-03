package tn.esprit.tn.medicare_ai.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;

public interface HealthEventRepository extends JpaRepository<HealthEvent, Long> {
}
