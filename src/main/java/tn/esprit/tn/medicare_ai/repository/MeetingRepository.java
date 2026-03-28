package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByOrganizerId(Long organizerId);

    List<Meeting> findByRecordedTrue();
}
