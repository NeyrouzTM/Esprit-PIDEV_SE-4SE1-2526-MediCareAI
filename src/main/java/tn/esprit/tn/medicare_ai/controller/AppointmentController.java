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
import tn.esprit.tn.medicare_ai.dto.AppointmentReminderDto;
import tn.esprit.tn.medicare_ai.dto.ReminderDeliveryResult;
import tn.esprit.tn.medicare_ai.dto.ReminderScheduleRequest;
import tn.esprit.tn.medicare_ai.dto.scheduling.AppointmentMatchCandidateDto;
import tn.esprit.tn.medicare_ai.dto.scheduling.AppointmentMatchRequestPayload;
import tn.esprit.tn.medicare_ai.dto.scheduling.AvailabilityConflictCheckRequest;
import tn.esprit.tn.medicare_ai.dto.scheduling.AvailabilityConflictDto;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.AppointmentReminderService;
import tn.esprit.tn.medicare_ai.service.AppointmentSchedulingService;
import tn.esprit.tn.medicare_ai.service.AppointmentService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentSchedulingService appointmentSchedulingService;
    private final AppointmentReminderService appointmentReminderService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','DOCTOR')")
    public ResponseEntity<?> create(@RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.create(dto, getCurrentUserId(), getCurrentUserRole()));
    }

    @PostMapping("/availability-conflicts")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<List<AvailabilityConflictDto>> detectAvailabilityConflicts(
            @RequestBody AvailabilityConflictCheckRequest body) {
        return ResponseEntity.ok(appointmentSchedulingService.detectConflicts(body));
    }

    @PostMapping("/matching/recommendations")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<List<AppointmentMatchCandidateDto>> recommendAppointmentMatches(
            @RequestBody AppointmentMatchRequestPayload body) {
        return ResponseEntity.ok(appointmentSchedulingService.recommendMatches(body));
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

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<?> searchByKeywords(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String patientKeyword,
            @RequestParam(required = false) String reasonKeyword) {
        return ResponseEntity.ok(appointmentService.searchByKeywords(
                doctorId,
                patientKeyword,
                reasonKeyword,
                getCurrentUserId(),
                getCurrentUserRole()
        ));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<?> getUpcomingByDoctorKeyword(
            @RequestParam(defaultValue = "") String doctorKeyword,
            @RequestParam(defaultValue = "30") int windowMinutes) {
        return ResponseEntity.ok(appointmentService.findUpcomingForDoctorKeyword(doctorKeyword, windowMinutes));
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
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<List<AppointmentReminderDto>> getReminders(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentReminderService.listReminders(appointmentId, getCurrentUserId(), getCurrentUserRole()));
    }

    @PostMapping("/{appointmentId}/reminders/schedule")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentReminderDto> scheduleReminder(
            @PathVariable Long appointmentId,
            @RequestBody ReminderScheduleRequest body) {
        return ResponseEntity.ok(appointmentReminderService.schedule(appointmentId, body, getCurrentUserId(), getCurrentUserRole()));
    }

    @PostMapping("/{appointmentId}/reminders/send")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ReminderDeliveryResult> sendReminder(
            @PathVariable Long appointmentId,
            @RequestBody(required = false) ReminderScheduleRequest body) {
        String ch = body != null && body.channel() != null ? body.channel() : "EMAIL";
        String pr = body != null && body.provider() != null ? body.provider() : "LOCAL";
        return ResponseEntity.ok(appointmentReminderService.sendNow(appointmentId, ch, pr, getCurrentUserId(), getCurrentUserRole()));
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