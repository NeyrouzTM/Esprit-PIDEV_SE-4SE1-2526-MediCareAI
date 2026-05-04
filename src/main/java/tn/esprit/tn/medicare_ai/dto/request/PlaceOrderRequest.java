package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PlaceOrderRequest", description = "Request payload used by a patient to place an MedicineOrder.")
public class PlaceOrderRequest {
    @Schema(description = "Shipping address for delivery.", example = "12 Main St, Tunis")
    @NotBlank
    @Size(max = 255)
    private String shippingAddress;

    @Schema(description = "MedicineOrder items to purchase.")
    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    @Schema(description = "Optional prescription identifier linked to this MedicineOrder.", example = "100")
    private Long prescriptionId;
}

