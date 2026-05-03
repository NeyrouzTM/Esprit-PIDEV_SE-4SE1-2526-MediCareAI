package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepRequest {

    @NotNull(message = "Sleep hours are required")
    @DecimalMin(value = "0.5", message = "Sleep hours must be at least 0.5")
    @DecimalMax(value = "24.0", message = "Sleep hours cannot exceed 24")
    private Float hours;

    @NotNull(message = "Sleep quality is required")
    @Min(value = 1, message = "Quality must be at least 1")
    @Max(value = 10, message = "Quality must be at most 10")
    private Integer quality;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
