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
@Schema(name = "InventoryResponse", description = "Inventory status for a medicine.")
public class InventoryResponse {
    @Schema(description = "Medicine identifier.", example = "10")
    private Long medicineId;
    @Schema(description = "Medicine name.", example = "Doliprane")
    private String medicineName;
    @Schema(description = "Current stock quantity.", example = "120")
    private Integer stockQuantity;
    @Schema(description = "Warehouse location code.", example = "WH-A1")
    private String warehouseLocation;
}
