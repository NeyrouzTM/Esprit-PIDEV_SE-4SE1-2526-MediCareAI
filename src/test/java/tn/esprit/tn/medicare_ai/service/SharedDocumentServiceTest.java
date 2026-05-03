package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.tn.medicare_ai.dto.request.SharedDocumentRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import tn.esprit.tn.medicare_ai.entity.CollaborationSession;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.SharedDocument;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.CollaborationSessionRepository;
import tn.esprit.tn.medicare_ai.repository.SharedDocumentRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.SharedDocumentServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SharedDocumentServiceTest {

    @Mock
    private SharedDocumentRepository documentRepository;

    @Mock
    private CollaborationSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SharedDocumentServiceImpl documentService;

    @Test
    @DisplayName("uploadDocument: valid request uploads document")
    void uploadDocument_validRequest_uploadsDocument() throws Exception {

        // 🔐 Simuler utilisateur connecté
        String email = "test@gmail.com";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null)
        );

        User uploader = new User();
        uploader.setId(1L);
        uploader.setFullName("Test Uploader");
        uploader.setRole(Role.DOCTOR);
        uploader.setEmail(email);

        CollaborationSession session = new CollaborationSession();
        session.setId(1L);

        // 📄 fichier mock
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );

        SharedDocument savedDocument = SharedDocument.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileUrl("uploads/test.pdf")
                .session(session)
                .uploader(uploader)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(uploader));
        when(documentRepository.save(any(SharedDocument.class))).thenReturn(savedDocument);

        SharedDocumentResponseDTO result =
                documentService.uploadDocument(file, "description", 1L);

        assertEquals(1L, result.getUploaderId());
        verify(documentRepository).save(any(SharedDocument.class));
    }

    @Test
    @DisplayName("getDocumentsBySession: returns documents for session")
    void getDocumentsBySession_returnsDocuments() {
        User uploader = new User();
        uploader.setId(1L);
        uploader.setFullName("Test Uploader");

        CollaborationSession session = new CollaborationSession();
        session.setId(1L);

        SharedDocument document = SharedDocument.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileUrl("http://example.com/test.pdf")
                .session(session)
                .uploader(uploader)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(documentRepository.findBySessionId(1L)).thenReturn(List.of(document));

        List<SharedDocumentResponseDTO> result = documentService.getDocumentsBySession(1L);

        assertEquals(1, result.size());
        assertEquals("test.pdf", result.get(0).getFileName());
    }

    @Test
    @DisplayName("getDocumentById: valid id returns document")
    void getDocumentById_validId_returnsDocument() {
        User uploader = new User();
        uploader.setId(1L);
        uploader.setFullName("Test Uploader");

        CollaborationSession session = new CollaborationSession();
        session.setId(1L);

        SharedDocument document = SharedDocument.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileUrl("http://example.com/test.pdf")
                .session(session)
                .uploader(uploader)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        SharedDocumentResponseDTO result = documentService.getDocumentById(1L);

        assertEquals("test.pdf", result.getFileName());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getDocumentById: invalid id throws exception")
    void getDocumentById_invalidId_throwsException() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> documentService.getDocumentById(1L));
    }
}
