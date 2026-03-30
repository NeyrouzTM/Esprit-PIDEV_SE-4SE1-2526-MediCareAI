package tn.esprit.tn.medicare_ai.controller;

import java.time.Instant;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.AppointmentService;

@RestController
@CrossOrigin("*")
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> create(@RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.create(dto, getCurrentUserId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(appointmentService.getAll(getCurrentUserRole(), getCurrentUserId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getByPatientId(patientId, getCurrentUserId(), getCurrentUserRole()));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<?> getByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getByDoctorId(doctorId, getCurrentUserId(), getCurrentUserRole()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.update(id, dto, getCurrentUserId(), getCurrentUserRole()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        appointmentService.delete(id, getCurrentUserId(), getCurrentUserRole());
        return ResponseEntity.ok("Appointment deleted");
    }

    @GetMapping("/{appointmentId}/reminders")
    public ResponseEntity<?> getReminders(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "NO_REMINDER_PROVIDER",
                "reminders", java.util.List.of()
        ));
    }

    @PostMapping("/{appointmentId}/reminders/schedule")
    public ResponseEntity<?> scheduleReminder(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "SCHEDULED",
                "scheduledAt", Instant.now().toString()
        ));
    }

    @PostMapping("/{appointmentId}/reminders/send")
    public ResponseEntity<?> sendReminder(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "SENT",
                "sentAt", Instant.now().toString()
        ));
    }

    @GetMapping("/{appointmentId}/teleconsultation")
    public ResponseEntity<?> getTeleconsultation(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "NOT_STARTED",
                "joinUrl", ""
        ));
    }

    @PostMapping("/{appointmentId}/teleconsultation/start")
    public ResponseEntity<?> startTeleconsultation(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "STARTED",
                "sessionId", "tele-" + appointmentId + "-" + Instant.now().toEpochMilli(),
                "startedAt", Instant.now().toString()
        ));
    }

    @PostMapping("/{appointmentId}/teleconsultation/join")
    public ResponseEntity<?> joinTeleconsultation(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(Map.of(
                "appointmentId", appointmentId,
                "status", "JOINED",
                "joinUrl", "https://tele.medicare-ai.local/session/" + appointmentId,
                "joinedAt", Instant.now().toString()
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