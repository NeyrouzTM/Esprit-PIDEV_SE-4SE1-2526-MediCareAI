package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.tn.medicare_ai.entity.RecommendationCategory;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendationRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    private String description;

    @NotBlank(message = "Goal is required")
    @Size(min = 3, max = 500, message = "Goal must be between 3 and 500 characters")
    private String goal;

    @NotNull(message = "Category is required — ACTIVITY, NUTRITION, HEALTH, OTHER")
    private RecommendationCategory category;

    @NotNull(message = "Pregnancy tracking ID is required")
    @Positive(message = "Pregnancy tracking ID must be positive")
    private Long pregnancyTrackingId;
}
