package tn.esprit.tn.medicare_ai.dto;

/**
 * Matches Angular POST /appointments/{id}/reminders/schedule body.
 */
public record ReminderScheduleRequest(
        String remindAt,
        String channel,
        String provider
) {
}
