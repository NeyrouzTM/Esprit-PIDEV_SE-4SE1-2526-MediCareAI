package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.service.VisitNoteService;

@RestController
@CrossOrigin("*")
@RequestMapping("/visit-notes")
@RequiredArgsConstructor
public class VisitNoteController {

    private final VisitNoteService visitNoteService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody VisitNoteDTO dto) {
        return ResponseEntity.ok(visitNoteService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(visitNoteService.getById(id));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<?> getByMedicalRecordId(
            @PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(
                visitNoteService.getByMedicalRecordId(medicalRecordId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody VisitNoteDTO dto) {
        return ResponseEntity.ok(visitNoteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        visitNoteService.delete(id);
        return ResponseEntity.ok("Visit note deleted");
    }
}