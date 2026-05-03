package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.tn.medicare_ai.entity.AlertLevel;
import tn.esprit.tn.medicare_ai.entity.AlertType;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertRequest {

    @NotNull(message = "Alert type is required")
    private AlertType type;

    @NotBlank(message = "Message is required")
    @Size(min = 5, max = 1000, message = "Message must be between 5 and 1000 characters")
    private String message;

    @NotNull(message = "Alert level is required")
    private AlertLevel level;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
