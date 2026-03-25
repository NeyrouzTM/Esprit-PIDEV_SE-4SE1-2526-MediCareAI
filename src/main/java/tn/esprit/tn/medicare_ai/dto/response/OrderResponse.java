package tn.esprit.tn.medicare_ai.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.OrderStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrderResponse", description = "Summary view of a patient order.")
public class OrderResponse {
    @Schema(description = "Order identifier.", example = "200")
    private Long id;

    @Schema(description = "Order creation date.", example = "2026-03-25")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @Schema(description = "Total order amount.", example = "45.7")
    private Double totalAmount;
    @Schema(description = "Order status.", example = "PENDING")
    private OrderStatus status;
    @Schema(description = "Shipment tracking number.", example = "TRK-20260325-01")
    private String trackingNumber;
    @Schema(description = "Number of order items.", example = "3")
    private Integer itemCount;
}
