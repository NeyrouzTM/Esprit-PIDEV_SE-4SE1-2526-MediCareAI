package tn.esprit.tn.medicare_ai.controller;



import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.request.SharedDocumentRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SharedDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaboration/documents")
public class SharedDocumentController {

    private final SharedDocumentService documentService;

    public SharedDocumentController(SharedDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/sessions/{sessionId}")
    public ResponseEntity<SharedDocumentResponseDTO> uploadDocument(
            @PathVariable Long sessionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        SharedDocumentResponseDTO created =
                documentService.uploadDocument(file, description, sessionId);

        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<List<SharedDocumentResponseDTO>> getDocumentsBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(documentService.getDocumentsBySession(sessionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedDocumentResponseDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @RequestParam Long uploaderId) {

        documentService.deleteDocument(id, uploaderId);
        return ResponseEntity.noContent().build();
    }
}