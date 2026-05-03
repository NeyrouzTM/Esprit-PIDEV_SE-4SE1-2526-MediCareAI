package tn.esprit.tn.medicare_ai.repository.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<User, Long> {

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
