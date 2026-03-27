package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RefillRequestDto", description = "Request payload for submitting a prescription refill.")
public class RefillRequestDto {
    @Schema(description = "Prescription identifier to refill.", example = "100")
    @NotNull
    private Long prescriptionId;

    @Schema(description = "Requested refill quantity.", example = "2")
    @Min(1)
    private Integer requestedQuantity;
}
