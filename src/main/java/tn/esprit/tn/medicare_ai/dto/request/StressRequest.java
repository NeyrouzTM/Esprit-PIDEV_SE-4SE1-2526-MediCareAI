package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StressRequest {

    @NotNull(message = "Stress level is required")
    @Min(value = 1, message = "Stress level must be at least 1")
    @Max(value = 10, message = "Stress level must be at most 10")
    private Integer level;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
