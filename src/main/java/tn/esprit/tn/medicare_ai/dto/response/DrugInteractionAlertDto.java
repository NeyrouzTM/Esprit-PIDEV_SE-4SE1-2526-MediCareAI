package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DrugInteractionAlertDto", description = "A detected interaction between two medicines.")
public class DrugInteractionAlertDto {
    @Schema(description = "First medicine name.", example = "Aspirin")
    private String medicineAName;
    @Schema(description = "Second medicine name.", example = "Warfarin")
    private String medicineBName;
    @Schema(description = "Interaction severity.", example = "SEVERE")
    private String severity;
    @Schema(description = "Interaction description.", example = "Increased bleeding risk.")
    private String description;
    @Schema(description = "Recommended action.", example = "Avoid this combination.")
    private String recommendation;
}
