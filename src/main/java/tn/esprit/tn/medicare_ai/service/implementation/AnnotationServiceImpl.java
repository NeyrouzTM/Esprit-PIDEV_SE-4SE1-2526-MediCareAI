package tn.esprit.tn.medicare_ai.service.implementation;


import tn.esprit.tn.medicare_ai.dto.request.AnnotationRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.AnnotationResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Annotation;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.SharedDocument;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AnnotationRepository;
import tn.esprit.tn.medicare_ai.repository.SharedDocumentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.AnnotationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    private final AnnotationRepository annotationRepository;
    private final SharedDocumentRepository documentRepository;
    private final UserRepository userRepository;

    public AnnotationServiceImpl(AnnotationRepository annotationRepository,
                                 SharedDocumentRepository documentRepository,
                                 UserRepository userRepository) {
        this.annotationRepository = annotationRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public AnnotationResponseDTO createAnnotation(AnnotationRequestDTO dto, Long documentId, Long authorId) {
        SharedDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));
        // Vérification logique : seul un professionnel ou admin peut annoter un document médical
        if (author.getRole() != Role.DOCTOR && author.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Seuls les professionnels peuvent annoter un document médical");
        }

        Annotation annotation = Annotation.builder()
                .content(dto.getContent())
                .positionX(dto.getPositionX())
                .positionY(dto.getPositionY())
                .document(document)
                .author(author)
                .build();

        Annotation saved = annotationRepository.save(annotation);
        return mapToResponseDTO(saved);
    }
    @Override
    public List<AnnotationResponseDTO> getAnnotationsByDocument(Long documentId) {
        return annotationRepository.findByDocumentId(documentId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnnotationResponseDTO getAnnotationById(Long id) {
        Annotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annotation non trouvée"));
        return mapToResponseDTO(annotation);
    }

    @Override
    @Transactional
    public AnnotationResponseDTO updateAnnotation(Long id, AnnotationRequestDTO dto, Long authorId) {
        Annotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annotation non trouvée"));

        if (!annotation.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("Seul l'auteur peut modifier cette annotation");
        }

        annotation.setContent(dto.getContent());
        annotation.setPositionX(dto.getPositionX());
        annotation.setPositionY(dto.getPositionY());

        Annotation updated = annotationRepository.save(annotation);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteAnnotation(Long id, Long authorId) {
        Annotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annotation non trouvée"));

        if (!annotation.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("Seul l'auteur peut supprimer cette annotation");
        }

        annotationRepository.delete(annotation);
    }

    private AnnotationResponseDTO mapToResponseDTO(Annotation annotation) {
        AnnotationResponseDTO dto = new AnnotationResponseDTO();
        dto.setId(annotation.getId());
        dto.setContent(annotation.getContent());
        dto.setPositionX(annotation.getPositionX());
        dto.setPositionY(annotation.getPositionY());
        dto.setDocumentId(annotation.getDocument().getId());
        dto.setAuthorId(annotation.getAuthor().getId());
        dto.setAuthorName(annotation.getAuthor().getFullName());
        dto.setCreatedAt(annotation.getCreatedAt());
        return dto;
    }}

