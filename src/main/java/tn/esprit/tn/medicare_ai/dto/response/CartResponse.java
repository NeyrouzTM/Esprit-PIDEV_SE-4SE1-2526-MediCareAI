package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CartResponse", description = "Complete shopping cart response.")
public class CartResponse {
    @Schema(description = "Cart line items.")
    @Builder.Default
    private List<CartItemResponse> items = List.of();

    @Schema(description = "Cart total amount.", example = "45.7")
    private Double totalAmount;
}
