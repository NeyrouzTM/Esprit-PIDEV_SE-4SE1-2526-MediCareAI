package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PrescriptionItemRequest", description = "Medicine line in a prescription creation request.")
public class PrescriptionItemRequest {
    @Schema(description = "Medicine identifier.", example = "10")
    @NotNull
    private Long medicineId;

    @Schema(description = "Prescribed quantity.", example = "1")
    @NotNull
    @Min(1)
    private Integer quantity;

    @Schema(description = "Dosage instructions.", example = "500mg")
    @NotBlank
    @Size(max = 100)
    private String dosage;

    @Schema(description = "Frequency of intake.", example = "Twice daily")
    @NotBlank
    @Size(max = 100)
    private String frequency;

    @Schema(description = "Prescription duration in days.", example = "7")
    @NotNull
    @Min(1)
    private Integer durationDays;

    @Schema(description = "Additional patient instructions.", example = "Take after meals")
    @Size(max = 500)
    private String instructions;

    @Schema(description = "Allowed refill count.", example = "1")
    @Min(0)
    private Integer refills;
}
