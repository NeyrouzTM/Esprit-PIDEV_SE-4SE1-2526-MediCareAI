package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.Alert;
import tn.esprit.tn.medicare_ai.entity.AlertLevel;
import tn.esprit.tn.medicare_ai.entity.AlertType;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Find alerts by user
    List<Alert> findByUserId(Long userId);

    // Find alerts by user and not ignored
    List<Alert> findByUserIdAndIgnoredFalse(Long userId);

    // Find alerts by user and type
    List<Alert> findByUserIdAndType(Long userId, AlertType type);

    // Find alerts by user and level
    List<Alert> findByUserIdAndLevel(Long userId, AlertLevel level);

    // Find urgent alerts for a user
    List<Alert> findByUserIdAndLevelAndIgnoredFalse(Long userId, AlertLevel level);

    // Find ignored alerts older than given date
    @Query("SELECT a FROM Alert a WHERE a.userId = :userId AND a.ignored = true " +
            "AND a.ignoredAt <= :dateTime")
    List<Alert> findIgnoredAlertsOlderThan(Long userId, LocalDateTime dateTime);

    // Find alerts created in last N days for a user
    @Query("SELECT a FROM Alert a WHERE a.userId = :userId AND a.createdAt >= :fromDate")
    List<Alert> findAlertsCreatedAfter(Long userId, LocalDateTime fromDate);
}
