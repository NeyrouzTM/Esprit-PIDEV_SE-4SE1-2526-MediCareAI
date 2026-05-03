package tn.esprit.tn.medicare_ai.service.implementation;
import tn.esprit.tn.medicare_ai.dto.request.CollaborationSessionRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.CollaborationSession;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.CollaborationSessionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.CollaborationSessionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CollaborationSessionServiceImpl implements CollaborationSessionService {

    private final CollaborationSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public CollaborationSessionServiceImpl(CollaborationSessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }


@Override
@Transactional
public CollaborationSessionResponseDTO createSession(CollaborationSessionRequestDTO dto, Long creatorId) {
    User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new EntityNotFoundException("Créateur non trouvé"));

    if (creator.getRole() != Role.DOCTOR) {
        throw new IllegalArgumentException("Seuls les professionnels peuvent créer une session de collaboration");
    }

    CollaborationSession session = CollaborationSession.builder()
            .title(dto.getTitle())
            .creator(creator)
            .build();

    CollaborationSession saved = sessionRepository.save(session);
    return mapToResponseDTO(saved);
}

@Override
public List<CollaborationSessionResponseDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
}

@Override
public CollaborationSessionResponseDTO getSessionById(Long id) {
        CollaborationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session avec ID " + id + " non trouvée"));
    return mapToResponseDTO(session);
}

@Override
@Transactional
public CollaborationSessionResponseDTO updateSession(Long id, CollaborationSessionRequestDTO dto, Long creatorId) {
        CollaborationSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

        if (!session.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("Seul le créateur peut modifier cette session");
        }

        session.setTitle(dto.getTitle());

        CollaborationSession updated = sessionRepository.save(session);
        return mapToResponseDTO(updated);
}

@Override
@Transactional
public void deleteSession(Long id, Long creatorId) {
    CollaborationSession session = sessionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

    if (!session.getCreator().getId().equals(creatorId)) {
        throw new IllegalArgumentException("Seul le créateur peut supprimer cette session");
    }

    sessionRepository.delete(session);
}
    @Override
    @Transactional
    public void addParticipantByEmail(Long sessionId, String email) {

        CollaborationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // 🔥 CHECK DB (FIABLE)
        boolean alreadyExists = sessionRepository
                .existsByIdAndParticipants_Id(sessionId, user.getId());

        if (alreadyExists) {
            throw new IllegalArgumentException("Utilisateur déjà participant");
        }

        session.getParticipants().add(user);

        sessionRepository.save(session);
    }

private CollaborationSessionResponseDTO mapToResponseDTO(CollaborationSession session) {
    CollaborationSessionResponseDTO dto = new CollaborationSessionResponseDTO();
    dto.setId(session.getId());
    dto.setTitle(session.getTitle());
    dto.setCreatorId(session.getCreator().getId());
    dto.setCreatorName(session.getCreator().getFullName());
    dto.setCreatedAt(session.getCreatedAt());

    dto.setParticipantIds(session.getParticipants() != null
            ? session.getParticipants().stream().map(User::getId).collect(Collectors.toSet())
            : new HashSet<>());

    dto.setDocumentCount(session.getDocuments() != null ? session.getDocuments().size() : 0);

    return dto;
}}
