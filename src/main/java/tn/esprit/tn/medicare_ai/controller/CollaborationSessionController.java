package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.request.CollaborationSessionRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.CollaborationSessionResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.CollaborationSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaboration/sessions")
public class CollaborationSessionController {

    private final CollaborationSessionService sessionService;

    public CollaborationSessionController(CollaborationSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<CollaborationSessionResponseDTO> createSession(
            @Valid @RequestBody CollaborationSessionRequestDTO dto,
            @RequestParam Long creatorId) {

        CollaborationSessionResponseDTO created = sessionService.createSession(dto, creatorId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CollaborationSessionResponseDTO>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollaborationSessionResponseDTO> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollaborationSessionResponseDTO> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody CollaborationSessionRequestDTO dto,
            @RequestParam Long creatorId) {

        return ResponseEntity.ok(sessionService.updateSession(id, dto, creatorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            @RequestParam Long creatorId) {

        sessionService.deleteSession(id, creatorId);
        return ResponseEntity.noContent().build();
    }
}
