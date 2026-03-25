package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DrugInteractionCheckResponse", description = "Result of the interaction checking process.")
public class DrugInteractionCheckResponse {
    @Schema(description = "Detected interaction alerts.")
    @Builder.Default
    private List<DrugInteractionAlertDto> alerts = List.of();

    @Schema(description = "True when at least one severe interaction exists.", example = "true")
    private boolean hasSevereInteraction;
}
