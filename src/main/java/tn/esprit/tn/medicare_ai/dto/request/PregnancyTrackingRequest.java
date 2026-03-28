package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyTrackingRequest {

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @NotNull(message = "Current week is required")
    @Min(value = 1, message = "Current week must be at least 1")
    @Max(value = 42, message = "Current week cannot exceed 42")
    private Integer currentWeek;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
