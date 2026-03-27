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
@Schema(name = "AddToCartRequest", description = "Request payload for adding a medicine to cart.")
public class AddToCartRequest {
    @Schema(description = "Medicine identifier.", example = "10")
    @NotNull
    private Long medicineId;

    @Schema(description = "Requested quantity.", example = "2")
    @NotNull
    @Min(1)
    private Integer quantity;
}
