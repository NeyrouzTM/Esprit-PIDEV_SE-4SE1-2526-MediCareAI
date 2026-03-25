package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.MedicinieCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MedicineSearchRequest", description = "Query parameters used to search and filter medicines.")
public class MedicineSearchRequest {
    @Schema(description = "Keyword applied to medicine name or generic name.", example = "paracetamol")
    @Size(max = 100)
    private String keyword;

    @Schema(description = "Filter by medicine category.", example = "ANALGESIC")
    private MedicinieCategory category;

    @Schema(description = "Whether prescription is required.", example = "true")
    private Boolean prescriptionRequired;

    @Schema(description = "Page number (0-based).", example = "0")
    @Min(0)
    private Integer page;

    @Schema(description = "Page size.", example = "20")
    @Min(1)
    @Max(100)
    private Integer size;
}
