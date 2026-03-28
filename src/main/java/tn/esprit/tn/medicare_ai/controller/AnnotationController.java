package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.request.AnnotationRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.AnnotationResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.AnnotationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaboration/annotations")
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @PostMapping("/documents/{documentId}")
    public ResponseEntity<AnnotationResponseDTO> createAnnotation(
            @PathVariable Long documentId,
            @Valid @RequestBody AnnotationRequestDTO dto,
            @RequestParam Long authorId) {

        AnnotationResponseDTO created = annotationService.createAnnotation(dto, documentId, authorId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<List<AnnotationResponseDTO>> getAnnotationsByDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(annotationService.getAnnotationsByDocument(documentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnotationResponseDTO> getAnnotationById(@PathVariable Long id) {
        return ResponseEntity.ok(annotationService.getAnnotationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnotationResponseDTO> updateAnnotation(
            @PathVariable Long id,
            @Valid @RequestBody AnnotationRequestDTO dto,
            @RequestParam Long authorId) {

        return ResponseEntity.ok(annotationService.updateAnnotation(id, dto, authorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnotation(
            @PathVariable Long id,
            @RequestParam Long authorId) {

        annotationService.deleteAnnotation(id, authorId);
        return ResponseEntity.noContent().build();
    }
}