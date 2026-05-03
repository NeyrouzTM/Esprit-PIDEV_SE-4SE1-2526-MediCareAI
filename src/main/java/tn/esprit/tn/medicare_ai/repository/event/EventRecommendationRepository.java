package tn.esprit.tn.medicare_ai.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.User;

import java.util.List;

/**
 * Queries for event-based recommendations (user + health events).
 * Named distinct from {@link tn.esprit.tn.medicare_ai.repository.RecommendationRepository}
 * (pregnancy {@code Recommendation} entity) to avoid duplicate Spring bean names.
 */
public interface EventRecommendationRepository extends JpaRepository<User, Long> {

    @Query("""
           select u from User u
           left join fetch u.events
           where u.id = :userId
           """)
    User findUserWithEvents(@Param("userId") Long userId);

    @Query("""
           select e.category, count(e) as count
           from User u
           join u.events e
           where u.id = :userId
           group by e.category
           order by count desc
           """)
    List<Object[]> getCategoryFrequencyForUser(@Param("userId") Long userId);
}
