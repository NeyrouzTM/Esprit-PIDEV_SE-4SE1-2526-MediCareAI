package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.dto.response.CorrelationDTO;
import tn.esprit.tn.medicare_ai.entity.Sleep;
import java.util.List;

@Repository
public interface SleepRepository extends JpaRepository<Sleep, Long> {
    List<Sleep> findByUserId(Long userId);

    @Query("""
            select distinct new tn.esprit.tn.medicare_ai.dto.response.CorrelationDTO(
                s.date,
                s.hours,
                m.level,
                st.level
            )
            from Sleep s
            join s.user u
            join Mood m on m.user = u and m.date = s.date
            join Stress st on st.user = u and st.date = s.date
            where u.id = :userId
            order by s.date asc
            """)
    List<CorrelationDTO> findCorrelationsByUserId(@Param("userId") Long userId);
}
