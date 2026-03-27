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
@Schema(name = "InventoryUpdateRequest", description = "Request payload for updating medicine stock quantity.")
public class InventoryUpdateRequest {
    @Schema(description = "Medicine identifier.", example = "10")
    @NotNull
    private Long medicineId;

    @Schema(description = "New stock quantity.", example = "120")
    @NotNull
    @Min(0)
    private Integer stockQuantity;
}
