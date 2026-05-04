package tn.esprit.tn.medicare_ai.dto;

/**
 * Mirrors frontend AppointmentReminderDTO (camelCase JSON).
 */
public record AppointmentReminderDto(
        Long id,
        Long appointmentId,
        String remindAt,
        String channel,
        String provider,
        String destination,
        String templateId,
        String status,
        String sentAt,
        String providerMessageId,
        String failureReason
) {
}
