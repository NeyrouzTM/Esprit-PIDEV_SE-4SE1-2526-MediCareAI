package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationReminderRequest {

    @NotNull(message = "Reminder time is required")
    private LocalTime time;

    @NotBlank(message = "Message is required")
    @Size(min = 3, max = 500, message = "Message must be between 3 and 500 characters")
    private String message;

    @NotNull(message = "Medication schedule ID is required")
    @Positive(message = "Medication schedule ID must be positive")
    private Long medicationScheduleId;
}
