package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.MedicalRecordService;

@RestController
@CrossOrigin("*")
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> create(@RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(medicalRecordService.create(dto, getCurrentUserId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medicalRecordService.getAll(getCurrentUserRole(), getCurrentUserId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getById(id, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatientId(patientId, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMine() {
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(medicalRecordService.getByPatientId(currentUserId, currentUserId, "PATIENT"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(medicalRecordService.update(id, dto, getCurrentUserId(), getCurrentUserRole()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        medicalRecordService.delete(id, getCurrentUserId(), getCurrentUserRole());
        return ResponseEntity.ok("Medical record deleted");
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