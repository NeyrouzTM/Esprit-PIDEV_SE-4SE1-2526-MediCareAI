package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityRequest {

    @NotBlank(message = "Activity type is required")
    @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters")
    private String type;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    private Integer duration;

    @NotBlank(message = "Benefit is required")
    @Size(min = 3, max = 500, message = "Benefit must be between 3 and 500 characters")
    private String benefit;

    @NotNull(message = "Pregnancy tracking ID is required")
    @Positive(message = "Pregnancy tracking ID must be positive")
    private Long pregnancyTrackingId;
}
