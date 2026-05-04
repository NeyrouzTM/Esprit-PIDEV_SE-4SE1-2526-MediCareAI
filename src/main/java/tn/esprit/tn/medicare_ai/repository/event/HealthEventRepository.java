package tn.esprit.tn.medicare_ai.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthEventRepository extends JpaRepository<HealthEvent, Long> {

    @Query("SELECT e FROM HealthEvent e WHERE e.eventDate > :now AND e.eventDate <= :limit")
    List<HealthEvent> findEventsForReminder(@Param("now") LocalDateTime now, @Param("limit") LocalDateTime limit);
}
