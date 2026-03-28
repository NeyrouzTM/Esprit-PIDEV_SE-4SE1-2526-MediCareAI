package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import tn.esprit.tn.medicare_ai.entity.RecommendationCategory;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendationResponse {
    private Long id;
    private String description;
    private String goal;
    private RecommendationCategory category;
    private Long pregnancyTrackingId;
}
