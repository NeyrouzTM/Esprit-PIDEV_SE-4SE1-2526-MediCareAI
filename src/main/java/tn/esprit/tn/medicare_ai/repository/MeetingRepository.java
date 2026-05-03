package tn.esprit.tn.medicare_ai.repository;

import tn.esprit.tn.medicare_ai.entity.Meeting;
import tn.esprit.tn.medicare_ai.entity.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByOrganizerId(Long organizerId);

    List<Meeting> findByRecordedTrue();

    List<Meeting> findByStatus(MeetingStatus status);

    /** Réunions auxquelles un utilisateur participe (organisateur ou participant) */
    @Query("""
            SELECT DISTINCT m FROM Meeting m
            LEFT JOIN m.participants p
            WHERE m.organizer.id = :userId OR p.id = :userId
            ORDER BY m.dateTime DESC
            """)
    List<Meeting> findAllByUserId(@Param("userId") Long userId);

    /** Réunions en cours (LIVE) */
    @Query("SELECT m FROM Meeting m WHERE m.status = 'LIVE' ORDER BY m.startedAt DESC")
    List<Meeting> findLiveMeetings();

    /** Réunions terminées avec PV généré */
    @Query("SELECT m FROM Meeting m WHERE m.status = 'FINISHED' AND m.pvGenerated = true")
    List<Meeting> findFinishedWithPv();

    /** Charge une réunion avec ses participants (évite le N+1) */
    @Query("""
            SELECT DISTINCT m FROM Meeting m
            LEFT JOIN FETCH m.participants
            LEFT JOIN FETCH m.organizer
            WHERE m.id = :id
            """)
    Optional<Meeting> findByIdWithParticipants(@Param("id") Long id);
}
