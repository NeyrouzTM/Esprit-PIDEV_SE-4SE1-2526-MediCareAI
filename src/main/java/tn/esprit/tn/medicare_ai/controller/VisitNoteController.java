package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.VisitNoteService;

@RestController
@CrossOrigin("*")
@RequestMapping("/visit-notes")
@RequiredArgsConstructor
public class VisitNoteController {

    private final VisitNoteService visitNoteService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> create(@RequestBody VisitNoteDTO dto) {
        return ResponseEntity.ok(visitNoteService.create(dto, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(visitNoteService.getById(id, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getByMedicalRecordId(@PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(visitNoteService.getByMedicalRecordId(medicalRecordId, getCurrentUserId(), getCurrentUserRole()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody VisitNoteDTO dto) {
        return ResponseEntity.ok(visitNoteService.update(id, dto, getCurrentUserId(), getCurrentUserRole()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        visitNoteService.delete(id, getCurrentUserId(), getCurrentUserRole());
        return ResponseEntity.ok("Visit note deleted");
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> searchClinicalNotes(
            @RequestParam(required = false) String patientKeyword,
            @RequestParam(required = false) String doctorKeyword,
            @RequestParam(required = false) String clinicalKeyword) {
        return ResponseEntity.ok(visitNoteService.searchClinicalNotes(
                patientKeyword,
                doctorKeyword,
                clinicalKeyword,
                getCurrentUserId(),
                getCurrentUserRole()
        ));
    }

    private Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private String getCurrentUserRole() {
        return getCurrentUser().getRole().name();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}