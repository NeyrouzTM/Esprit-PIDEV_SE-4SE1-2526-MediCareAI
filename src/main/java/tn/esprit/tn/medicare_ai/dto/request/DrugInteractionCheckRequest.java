package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "DrugInteractionCheckRequest", description = "Request payload to check medicine interaction alerts.")
public class DrugInteractionCheckRequest {
    @Schema(description = "List of medicine identifiers to check.", example = "[1,2]")
    @NotEmpty
    @Size(min = 2)
    private List<@NotNull Long> medicineIds;

    @Schema(description = "Optional patient identifier to include active medications.", example = "42")
    private Long patientId;
}
