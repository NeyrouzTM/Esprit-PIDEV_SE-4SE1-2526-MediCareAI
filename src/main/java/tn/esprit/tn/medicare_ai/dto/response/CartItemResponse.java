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
@Schema(name = "CartItemResponse", description = "Single item in the shopping cart.")
public class CartItemResponse {
    @Schema(description = "Medicine identifier.", example = "10")
    private Long medicineId;
    @Schema(description = "Medicine name.", example = "Doliprane")
    private String medicineName;
    @Schema(description = "Quantity currently in cart.", example = "2")
    private Integer quantity;
    @Schema(description = "Unit price.", example = "8.5")
    private Double unitPrice;
    @Schema(description = "Line subtotal.", example = "17.0")
    private Double subtotal;
    @Schema(description = "Whether this item requires prescription.", example = "false")
    private Boolean prescriptionRequired;
}
