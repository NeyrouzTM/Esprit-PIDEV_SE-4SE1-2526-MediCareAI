package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrderDetailResponse", description = "Detailed order response including all line items.")
public class OrderDetailResponse extends OrderResponse {
    @Schema(description = "Order line items.")
    private List<OrderItemResponse> items = List.of();

    @Schema(description = "Shipping address.", example = "12 Main St, Tunis")
    private String shippingAddress;
    @Schema(description = "Linked prescription identifier when applicable.", example = "100")
    private Long prescriptionId;

    @Builder(builderMethodName = "orderDetailBuilder")
    public OrderDetailResponse(Long id,
                               LocalDate orderDate,
                               Double totalAmount,
                               OrderStatus status,
                               String trackingNumber,
                               Integer itemCount,
                               List<OrderItemResponse> items,
                               String shippingAddress,
                               Long prescriptionId) {
        super(id, orderDate, totalAmount, status, trackingNumber, itemCount);
        this.items = items != null ? items : List.of();
        this.shippingAddress = shippingAddress;
        this.prescriptionId = prescriptionId;
    }
}
