package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.ReminderDeliveryResult;
import tn.esprit.tn.medicare_ai.dto.ReminderScheduleRequest;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.AppointmentReminderService;

/**
 * Optional integration paths used by the Angular client before falling back to /appointments/.../reminders/send.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/integrations")
@RequiredArgsConstructor
public class IntegrationsController {

    private final AppointmentReminderService appointmentReminderService;
    private final UserRepository userRepository;

    @PostMapping("/reminders/{appointmentId}/send")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ReminderDeliveryResult> sendReminder(
            @PathVariable Long appointmentId,
            @RequestBody(required = false) ReminderScheduleRequest body) {
        User u = currentUser();
        String ch = body != null && body.channel() != null ? body.channel() : "EMAIL";
        String pr = body != null && body.provider() != null ? body.provider() : "LOCAL";
        return ResponseEntity.ok(appointmentReminderService.sendNow(
                appointmentId, ch, pr, u.getId(), u.getRole().name()));
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
