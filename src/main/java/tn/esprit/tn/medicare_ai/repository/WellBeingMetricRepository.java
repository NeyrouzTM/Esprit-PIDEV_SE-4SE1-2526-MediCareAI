package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.WellBeingMetric;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WellBeingMetricRepository extends JpaRepository<WellBeingMetric, Long> {
    List<WellBeingMetric> findByUserId(Long userId);
    List<WellBeingMetric> findTop3ByUser_IdOrderByStartDateDesc(Long userId);

    @Query("""
        SELECT w.user, COUNT(w)
        FROM WellBeingMetric w
        WHERE w.startDate >= :since AND w.level = 'LOW'
        GROUP BY w.user.id
        HAVING COUNT(w) >= 3
    """)
    List<Object[]> findUsersWithLowLevel(@Param("since") LocalDate since);

    @Query("SELECT DISTINCT w.user.id FROM WellBeingMetric w")
    List<Long> findAllUserIds();
}
