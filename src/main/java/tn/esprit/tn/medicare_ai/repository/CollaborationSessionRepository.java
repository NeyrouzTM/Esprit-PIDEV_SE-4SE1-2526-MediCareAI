package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.CollaborationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationSessionRepository extends JpaRepository<CollaborationSession, Long> {

    List<CollaborationSession> findByCreatorId(Long creatorId);
    boolean existsByIdAndParticipants_Id(Long sessionId, Long userId);
}