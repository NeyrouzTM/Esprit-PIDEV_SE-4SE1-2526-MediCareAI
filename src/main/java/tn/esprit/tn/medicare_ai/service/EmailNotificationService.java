package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.entity.Appointment;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.mail.reminders-enabled:true}")
    private boolean remindersEnabled;

    /** Outcome of attempting to send mail (never throws — avoids HTTP 500 on SMTP/network errors). */
    public record MailSendAttempt(boolean success, String failureDetail) {
        public static MailSendAttempt ok() {
            return new MailSendAttempt(true, null);
        }

        public static MailSendAttempt fail(String detail) {
            return new MailSendAttempt(false, detail);
        }
    }

    /**
     * Send a plain-text appointment reminder to the patient. Caller persists reminder audit fields.
     * Mail errors (blocked SMTP, bad credentials, timeouts) are caught and returned as {@link MailSendAttempt#fail}.
     */
    public MailSendAttempt sendAppointmentReminder(Appointment appointment) {
        if (!remindersEnabled) {
            log.debug("Reminder emails disabled by configuration (app.mail.reminders-enabled=false)");
            return MailSendAttempt.fail("Reminders disabled (app.mail.reminders-enabled=false).");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Cannot send mail: spring.mail.username is empty. Set SMTP credentials in application.properties.");
            return MailSendAttempt.fail("SMTP not configured: set spring.mail.username and spring.mail.password (Gmail app password) in application.properties.");
        }
        String to = appointment.getPatient().getEmail();
        if (to == null || to.isBlank()) {
            log.warn("Patient {} has no email; skipping reminder for appointment {}", appointment.getPatient().getId(), appointment.getId());
            return MailSendAttempt.fail("Patient has no email address in the directory.");
        }

        String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "your practitioner";
        String when = appointment.getAppointmentDate() != null
                ? appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "(time TBD)";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject("MediCare AI — appointment reminder");
        msg.setText("""
                Hello,

                This is a reminder of your upcoming appointment on %s with %s.

                Reason / notes: %s

                Please arrive on time or join your teleconsultation link from the MediCare AI app.

                — MediCare AI (automated message)
                """.formatted(
                when,
                doctorName,
                appointment.getReason() != null ? appointment.getReason() : "—"
        ));

        try {
            mailSender.send(msg);
            log.info("Reminder email queued/sent for appointment id={} to {}", appointment.getId(), to);
            return MailSendAttempt.ok();
        } catch (MailException ex) {
            Throwable root = ex;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            String hint = root.getMessage() != null ? root.getMessage() : ex.getMessage();
            log.error("Mail send failed for appointment {}: {}", appointment.getId(), hint, ex);
            String userHint = hint != null && hint.toLowerCase().contains("timed out")
                    ? "Cannot reach mail server (connection timed out). Check firewall/VPN, allow outbound TCP 587 to smtp.gmail.com, or use a different network."
                    : "Mail server error: " + hint;
            return MailSendAttempt.fail(userHint);
        }
    }
}
