package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.AppointmentReminderDto;
import tn.esprit.tn.medicare_ai.dto.ReminderDeliveryResult;
import tn.esprit.tn.medicare_ai.dto.ReminderScheduleRequest;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.entity.AppointmentReminder;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.AppointmentReminderRepository;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private static final long MIN_LEAD_HOURS = 24;

    private final AppointmentRepository appointmentRepository;
    private final AppointmentReminderRepository reminderRepository;
    private final EmailNotificationService emailNotificationService;

    public List<AppointmentReminderDto> listReminders(Long appointmentId, Long currentUserId, String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        ensureCanAccess(appointment, currentUserId, role);
        return reminderRepository.findByAppointment_IdOrderByRemindAtAsc(appointmentId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AppointmentReminderDto schedule(Long appointmentId, ReminderScheduleRequest req, Long currentUserId, String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        ensureCanAccess(appointment, currentUserId, role);

        Instant remindAt = Instant.parse(req.remindAt());
        String channel = normalizeChannel(req.channel());
        String provider = normalizeProvider(channel, req.provider());

        validateScheduledReminder(appointment, remindAt);

        AppointmentReminder row = AppointmentReminder.builder()
                .appointment(appointment)
                .remindAt(remindAt)
                .channel(channel)
                .provider(provider)
                .status("SCHEDULED")
                .build();
        return toDto(reminderRepository.save(row));
    }

    @Transactional
    public ReminderDeliveryResult sendNow(Long appointmentId, String channelRaw, String providerRaw,
                                          Long currentUserId, String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        ensureCanAccess(appointment, currentUserId, role);

        String channel = normalizeChannel(channelRaw);
        String provider = normalizeProvider(channel, providerRaw);

        if ("EMAIL".equals(channel) && usesSpringMail(provider)) {
            EmailNotificationService.MailSendAttempt attempt = emailNotificationService.sendAppointmentReminder(appointment);
            if (!attempt.success()) {
                return new ReminderDeliveryResult(false, provider, channel, null, attempt.failureDetail());
            }
            String msgId = "mail-" + UUID.randomUUID();
            appointment.setReminderEmailCount(appointment.getReminderEmailCount() + 1);
            appointment.setReminderLastSentAt(LocalDateTime.now());
            appointmentRepository.save(appointment);
            return new ReminderDeliveryResult(true, provider, channel, msgId, "Reminder email sent via Spring Mail.");
        }

        return new ReminderDeliveryResult(true, provider, channel, "noop-" + appointmentId,
                "Reminder recorded (" + channel + " / " + provider + "). Email uses Gmail SMTP when provider is LOCAL.");
    }

    private void validateScheduledReminder(Appointment appointment, Instant remindAt) {
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment has no start time.");
        }
        Instant now = Instant.now();
        if (!remindAt.isAfter(now)) {
            throw new IllegalArgumentException("Reminder time must be in the future.");
        }
        Instant visitStart = appointment.getAppointmentDate().atZone(ZoneId.systemDefault()).toInstant();
        if (!remindAt.isBefore(visitStart)) {
            throw new IllegalArgumentException("The reminder must be before the appointment start.");
        }
        Duration lead = Duration.between(remindAt, visitStart);
        if (lead.toHours() < MIN_LEAD_HOURS) {
            throw new IllegalArgumentException("Reminder must be at least " + MIN_LEAD_HOURS + " hours before the visit.");
        }
    }

    private void ensureCanAccess(Appointment appointment, Long currentUserId, String role) {
        if ("ADMIN".equals(role)) {
            return;
        }
        if ("DOCTOR".equals(role) && appointment.getDoctor().getId().equals(currentUserId)) {
            return;
        }
        if ("PATIENT".equals(role) && appointment.getPatient().getId().equals(currentUserId)) {
            return;
        }
        throw new UnauthorizedActionException("Not allowed to manage reminders for this appointment");
    }

    private String normalizeChannel(String channel) {
        if (channel == null || channel.isBlank()) {
            return "EMAIL";
        }
        return channel.trim().toUpperCase();
    }

    /** Frontend sends LOCAL for Gmail/Spring Mail; legacy SENDGRID is treated as LOCAL for email. */
    private String normalizeProvider(String channel, String provider) {
        if ("SMS".equals(channel)) {
            return "TWILIO";
        }
        if ("EMAIL".equals(channel)) {
            if (provider == null || provider.isBlank()) {
                return "LOCAL";
            }
            String p = provider.trim().toUpperCase();
            if ("SENDGRID".equals(p)) {
                return "LOCAL";
            }
            return p;
        }
        return provider == null || provider.isBlank() ? "LOCAL" : provider.trim().toUpperCase();
    }

    private boolean usesSpringMail(String provider) {
        return provider == null || "LOCAL".equalsIgnoreCase(provider) || "SENDGRID".equalsIgnoreCase(provider);
    }

    private AppointmentReminderDto toDto(AppointmentReminder r) {
        return new AppointmentReminderDto(
                r.getId(),
                r.getAppointment().getId(),
                r.getRemindAt().toString(),
                r.getChannel(),
                r.getProvider(),
                null,
                null,
                r.getStatus(),
                r.getSentAt() != null ? r.getSentAt().toString() : null,
                r.getProviderMessageId(),
                r.getFailureReason()
        );
    }
}
