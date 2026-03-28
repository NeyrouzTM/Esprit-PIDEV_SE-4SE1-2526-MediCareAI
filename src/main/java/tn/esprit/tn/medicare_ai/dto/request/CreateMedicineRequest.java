package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.MedicinieCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateMedicineRequest", description = "Payload used by pharmacists to add a new medicine.")
public class CreateMedicineRequest {
    @NotBlank
    @Size(max = 120)
    @Schema(description = "Medicine brand name.", example = "Doliprane")
    private String name;

    @Size(max = 120)
    @Schema(description = "Generic medicine name.", example = "Paracetamol")
    private String genericName;

    @Size(max = 120)
    @Schema(description = "Manufacturer name.", example = "Sanofi")
    private String manufacturer;

    @Size(max = 1000)
    @Schema(description = "Detailed medicine description.", example = "Pain reliever and fever reducer.")
    private String description;

    @NotNull
    @Schema(description = "Medicine category.", example = "ANALGESIC")
    private MedicinieCategory category;

    @NotBlank
    @Size(max = 80)
    @Schema(description = "Dosage form.", example = "Tablet")
    private String dosageForm;

    @NotBlank
    @Size(max = 80)
    @Schema(description = "Strength.", example = "500mg")
    private String strength;

    @Size(max = 2000)
    @Schema(description = "Public image URL.", example = "https://cdn.medicare.ai/images/med-10.png")
    private String imageUrl;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Unit price.", example = "8.5")
    private Double price;

    @NotNull
    @Schema(description = "Whether a prescription is required.", example = "false")
    private Boolean prescriptionRequired;
}

