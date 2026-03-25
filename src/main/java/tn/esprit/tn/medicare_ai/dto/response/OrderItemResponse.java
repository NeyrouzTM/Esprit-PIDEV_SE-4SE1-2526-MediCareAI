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
@Schema(name = "OrderItemResponse", description = "Line item in an order response.")
public class OrderItemResponse {
    @Schema(description = "Order item identifier.", example = "900")
    private Long id;
    @Schema(description = "Medicine identifier.", example = "10")
    private Long medicineId;
    @Schema(description = "Medicine name.", example = "Doliprane")
    private String medicineName;
    @Schema(description = "Ordered quantity.", example = "2")
    private Integer quantity;
    @Schema(description = "Unit price at purchase time.", example = "8.5")
    private Double unitPrice;
    @Schema(description = "Line subtotal.", example = "17.0")
    private Double subtotal;
}
