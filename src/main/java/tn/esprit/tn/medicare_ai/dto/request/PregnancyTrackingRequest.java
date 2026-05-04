package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyTrackingRequest {

    @NotNull(message = "Start date (LMP) is required")
    @PastOrPresent(message = "Start date (LMP) cannot be in the future")
    private LocalDate startDate;

    // Optional - will be auto-calculated if not provided
    @Min(value = 1, message = "Current week must be at least 1")
    @Max(value = 42, message = "Current week cannot exceed 42")
    private Integer currentWeek;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    // Optional - will be auto-calculated if not provided
    // Due date = LMP + 280 days (standard pregnancy duration)
    private LocalDate dueDate;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
