package tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO;

import java.time.LocalDateTime;


public record ReminderResult(
        Long eventId,
        String eventTitle,
        LocalDateTime eventDate,
        String recipientEmail,
        String message
) {}