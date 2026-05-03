package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.CollaborationSessionRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.CollaborationSession;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.CollaborationSessionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.CollaborationSessionServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaborationSessionServiceTest {

    @Mock
    private CollaborationSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CollaborationSessionServiceImpl sessionService;

    @Test
    @DisplayName("createSession: valid request creates session")
    void createSession_validRequest_createsSession() {
        User creator = new User();
        creator.setId(1L);
        creator.setFullName("Test Creator");
        creator.setRole(Role.DOCTOR);

        CollaborationSessionRequestDTO request = CollaborationSessionRequestDTO.builder()
                .title("Test Session")
                .build();

        CollaborationSession savedSession = CollaborationSession.builder()
                .id(1L)
                .title("Test Session")
                .creator(creator)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(sessionRepository.save(any(CollaborationSession.class))).thenReturn(savedSession);

        CollaborationSessionResponseDTO result = sessionService.createSession(request, 1L);

        assertEquals("Test Session", result.getTitle());
        assertEquals(1L, result.getCreatorId());
        verify(sessionRepository).save(any(CollaborationSession.class));
    }

    @Test
    @DisplayName("getAllSessions: returns all sessions")
    void getAllSessions_returnsAllSessions() {
        User creator = new User();
        creator.setId(1L);
        creator.setFullName("Test Creator");

        CollaborationSession session = CollaborationSession.builder()
                .id(1L)
                .title("Test Session")
                .creator(creator)
                .createdAt(LocalDateTime.now())
                .build();

        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<CollaborationSessionResponseDTO> result = sessionService.getAllSessions();

        assertEquals(1, result.size());
        assertEquals("Test Session", result.get(0).getTitle());
    }

    @Test
    @DisplayName("getSessionById: valid id returns session")
    void getSessionById_validId_returnsSession() {
        User creator = new User();
        creator.setId(1L);
        creator.setFullName("Test Creator");

        CollaborationSession session = CollaborationSession.builder()
                .id(1L)
                .title("Test Session")
                .creator(creator)
                .createdAt(LocalDateTime.now())
                .build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        CollaborationSessionResponseDTO result = sessionService.getSessionById(1L);

        assertEquals("Test Session", result.getTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getSessionById: invalid id throws exception")
    void getSessionById_invalidId_throwsException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> sessionService.getSessionById(1L));
    }
}
