package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import tn.esprit.tn.medicare_ai.entity.AlertLevel;
import tn.esprit.tn.medicare_ai.entity.AlertType;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertResponse {
    private Long id;
    private AlertType type;
    private String message;
    private AlertLevel level;
    private Boolean ignored;
    private LocalDateTime ignoredAt;
    private Long userId;
    private LocalDateTime createdAt;

    // Nested objects
    private RecommendationResponse recommendation;
    private ActivityResponse activity;
}
