package tn.esprit.tn.medicare_ai.dto;

public record ReminderDeliveryResult(
        boolean success,
        String provider,
        String channel,
        String providerMessageId,
        String message
) {
}
