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
@Schema(name = "MedicineResponse", description = "Summary view of a medicine.")
public class MedicineResponse {
    @Schema(description = "Medicine identifier.", example = "10")
    private Long id;
    @Schema(description = "Medicine brand name.", example = "Doliprane")
    private String name;
    @Schema(description = "Generic medicine name.", example = "Paracetamol")
    private String genericName;
    @Schema(description = "Dosage form.", example = "Tablet")
    private String dosageForm;
    @Schema(description = "Strength.", example = "500mg")
    private String strength;
    @Schema(description = "Unit price.", example = "8.5")
    private Double price;
    @Schema(description = "Whether prescription is required.", example = "false")
    private Boolean prescriptionRequired;
    @Schema(description = "Public image URL.", example = "https://cdn.medicare.ai/images/med-10.png")
    private String imageUrl;
}
