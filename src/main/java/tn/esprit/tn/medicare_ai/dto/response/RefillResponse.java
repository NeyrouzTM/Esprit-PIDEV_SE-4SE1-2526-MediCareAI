package tn.esprit.tn.medicare_ai.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.RefillStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RefillResponse", description = "Refill request result or status payload.")
public class RefillResponse {
    @Schema(description = "Refill request identifier.", example = "55")
    private Long id;
    @Schema(description = "Prescription identifier.", example = "100")
    private Long prescriptionId;

    @Schema(description = "Date refill request was created.", example = "2026-03-25")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;

    @Schema(description = "Refill request status.", example = "PENDING")
    private RefillStatus status;
    @Schema(description = "Additional status message.", example = "Refill request submitted")
    private String message;
}
