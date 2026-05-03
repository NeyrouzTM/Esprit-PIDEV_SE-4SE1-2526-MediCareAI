package tn.esprit.tn.medicare_ai.service.interfaces;



import tn.esprit.tn.medicare_ai.dto.request.CollaborationSessionRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import java.util.List;

public interface CollaborationSessionService {

    CollaborationSessionResponseDTO createSession(CollaborationSessionRequestDTO dto, Long creatorId);
    List<CollaborationSessionResponseDTO> getAllSessions();
    CollaborationSessionResponseDTO getSessionById(Long id);
    CollaborationSessionResponseDTO updateSession(Long id, CollaborationSessionRequestDTO dto, Long creatorId);
    void deleteSession(Long id, Long creatorId);
    void addParticipantByEmail(Long sessionId, String email);
}
