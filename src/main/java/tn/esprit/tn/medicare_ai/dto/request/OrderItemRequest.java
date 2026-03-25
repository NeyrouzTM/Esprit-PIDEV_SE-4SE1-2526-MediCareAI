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
@Schema(name = "OrderItemRequest", description = "Line item in a place-order request.")
public class OrderItemRequest {
    @Schema(description = "Medicine identifier.", example = "10")
    @NotNull
    private Long medicineId;

    @Schema(description = "Quantity requested for this medicine.", example = "2")
    @NotNull
    @Min(1)
    private Integer quantity;

    @Schema(description = "Optional prescription item identifier for prescription-bound medicine.", example = "501")
    private Long prescriptionItemId;
}
