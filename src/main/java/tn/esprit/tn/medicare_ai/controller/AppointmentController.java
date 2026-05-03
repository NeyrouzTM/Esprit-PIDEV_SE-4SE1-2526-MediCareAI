package tn.esprit.tn.medicare_ai.controller;

import java.time.Instant;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AppointmentDTO;
import tn.esprit.tn.medicare_ai.service.AppointmentService;

@RestController
@CrossOrigin("*")
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatientId(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                appointmentService.getByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getByDoctorId(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentService.getByDoctorId(doctorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(
                appointmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        appointmentService.delete(id);
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
}