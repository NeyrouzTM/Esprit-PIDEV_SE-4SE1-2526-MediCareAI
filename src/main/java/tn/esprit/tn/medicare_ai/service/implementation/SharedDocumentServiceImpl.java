package tn.esprit.tn.medicare_ai.service.implementation;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public SharedDocumentResponseDTO uploadDocument(
            MultipartFile file,
            String description,
            Long sessionId) {

        // 🔍 Vérifier fichier
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier invalide");
        }

        // 🔍 Récupérer session
        CollaborationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

        // 🔐 Récupérer utilisateur connecté (IMPORTANT)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (uploader == null) {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }

        // 🔒 Vérification rôle
        if (uploader.getRole() != Role.DOCTOR && uploader.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Seuls les professionnels ou admins peuvent uploader");
        }

        // 📁 Générer nom fichier
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            // 📂 Sauvegarde locale (simple)
            String uploadDir = "uploads/";
            Path path = Paths.get(uploadDir + fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier");
        }

        // 💾 Sauvegarde DB
        SharedDocument document = SharedDocument.builder()
                .fileName(fileName)
                .fileUrl("uploads/" + fileName) // chemin simple
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