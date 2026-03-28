package tn.esprit.tn.medicare_ai.service.implementation;

import tn.esprit.tn.medicare_ai.dto.request.SharedDocumentRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import tn.esprit.tn.medicare_ai.entity.CollaborationSession;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.SharedDocument;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.CollaborationSessionRepository;
import tn.esprit.tn.medicare_ai.repository.SharedDocumentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.SharedDocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedDocumentServiceImpl implements SharedDocumentService {

    private final SharedDocumentRepository documentRepository;
    private final CollaborationSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SharedDocumentServiceImpl(SharedDocumentRepository documentRepository,
                                     CollaborationSessionRepository sessionRepository,
                                     UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public SharedDocumentResponseDTO uploadDocument(SharedDocumentRequestDTO dto, Long sessionId, Long uploaderId) {

        CollaborationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new EntityNotFoundException("Uploader non trouvé"));
        // Vérification logique
        if (uploader.getRole() != Role.DOCTOR && uploader.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Seuls les professionnels ou admins peuvent uploader des documents médicaux dans une session");
        }

        SharedDocument document = SharedDocument.builder()
                .fileName(dto.getFileName())
                .fileUrl(dto.getFileUrl())
                .session(session)
                .uploader(uploader)
                .build();

        SharedDocument saved = documentRepository.save(document);

        return mapToResponseDTO(saved);
    }

    @Override
    public List<SharedDocumentResponseDTO> getDocumentsBySession(Long sessionId) {
        return documentRepository.findBySessionId(sessionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SharedDocumentResponseDTO getDocumentById(Long id) {
        SharedDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

        return mapToResponseDTO(document);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id, Long uploaderId) {

        SharedDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

        if (!document.getUploader().getId().equals(uploaderId)) {
            throw new IllegalArgumentException("Seul l'uploader peut supprimer ce document");
        }

        documentRepository.delete(document);
    }

    private SharedDocumentResponseDTO mapToResponseDTO(SharedDocument document) {

        SharedDocumentResponseDTO dto = new SharedDocumentResponseDTO();

        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setFileUrl(document.getFileUrl());
        dto.setSessionId(document.getSession().getId());

        // ✅ IMPORTANT
        dto.setUploaderId(document.getUploader().getId());
        dto.setUploaderName(document.getUploader().getFullName());

        dto.setAnnotationCount(
                document.getAnnotations() != null
                        ? document.getAnnotations().size()
                        : 0
        );

        dto.setUploadedAt(document.getUploadedAt());

        return dto;
    }
}