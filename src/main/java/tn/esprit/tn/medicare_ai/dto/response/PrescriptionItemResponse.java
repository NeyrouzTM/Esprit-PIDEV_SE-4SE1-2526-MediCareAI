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
@Schema(name = "PrescriptionItemResponse", description = "Detailed medicine line in a prescription.")
public class PrescriptionItemResponse {
    @Schema(description = "Prescription item identifier.", example = "500")
    private Long id;
    @Schema(description = "Medicine identifier.", example = "10")
    private Long medicineId;
    @Schema(description = "Medicine name.", example = "Doliprane")
    private String medicineName;
    @Schema(description = "Medicine image URL.", example = "https://cdn.medicare.ai/images/med-10.png")
    private String medicineImageUrl;
    @Schema(description = "Prescribed quantity.", example = "1")
    private Integer quantity;
    @Schema(description = "Dosage instructions.", example = "500mg")
    private String dosage;
    @Schema(description = "Frequency of intake.", example = "Twice daily")
    private String frequency;
    @Schema(description = "Duration in days.", example = "7")
    private Integer durationDays;
    @Schema(description = "Additional instructions.", example = "Take after meals")
    private String instructions;
    @Schema(description = "Allowed refill count.", example = "1")
    private Integer refills;
}
