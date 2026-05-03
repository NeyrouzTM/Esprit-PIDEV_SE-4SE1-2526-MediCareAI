package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.AnnotationRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.AnnotationResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Annotation;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.SharedDocument;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AnnotationRepository;
import tn.esprit.tn.medicare_ai.repository.SharedDocumentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.AnnotationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnotationServiceTest {

    @Mock
    private AnnotationRepository annotationRepository;

    @Mock
    private SharedDocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnnotationServiceImpl annotationService;

    @Test
    @DisplayName("createAnnotation: valid request creates annotation")
    void createAnnotation_validRequest_createsAnnotation() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");
        author.setRole(Role.DOCTOR);

        SharedDocument document = new SharedDocument();
        document.setId(1L);

        AnnotationRequestDTO request = AnnotationRequestDTO.builder()
                .content("Test annotation")
                .positionX(10.0f)
                .positionY(20.0f)
                .build();

        Annotation savedAnnotation = Annotation.builder()
                .id(1L)
                .content("Test annotation")
                .positionX(10.0f)
                .positionY(20.0f)
                .document(document)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(annotationRepository.save(any(Annotation.class))).thenReturn(savedAnnotation);

        AnnotationResponseDTO result = annotationService.createAnnotation(request, 1L, 1L);

        assertEquals("Test annotation", result.getContent());
        assertEquals(1L, result.getAuthorId());
        verify(annotationRepository).save(any(Annotation.class));
    }

    @Test
    @DisplayName("getAnnotationsByDocument: returns annotations for document")
    void getAnnotationsByDocument_returnsAnnotations() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        SharedDocument document = new SharedDocument();
        document.setId(1L);

        Annotation annotation = Annotation.builder()
                .id(1L)
                .content("Test annotation")
                .positionX(10.0f)
                .positionY(20.0f)
                .document(document)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(annotationRepository.findByDocumentId(1L)).thenReturn(List.of(annotation));

        List<AnnotationResponseDTO> result = annotationService.getAnnotationsByDocument(1L);

        assertEquals(1, result.size());
        assertEquals("Test annotation", result.get(0).getContent());
    }

    @Test
    @DisplayName("getAnnotationById: valid id returns annotation")
    void getAnnotationById_validId_returnsAnnotation() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        SharedDocument document = new SharedDocument();
        document.setId(1L);

        Annotation annotation = Annotation.builder()
                .id(1L)
                .content("Test annotation")
                .positionX(10.0f)
                .positionY(20.0f)
                .document(document)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(annotationRepository.findById(1L)).thenReturn(Optional.of(annotation));

        AnnotationResponseDTO result = annotationService.getAnnotationById(1L);

        assertEquals("Test annotation", result.getContent());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getAnnotationById: invalid id throws exception")
    void getAnnotationById_invalidId_throwsException() {
        when(annotationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> annotationService.getAnnotationById(1L));
    }
}
