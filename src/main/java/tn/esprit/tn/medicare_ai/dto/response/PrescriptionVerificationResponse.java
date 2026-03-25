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
@Schema(name = "PrescriptionVerificationResponse", description = "Response after prescription image submission.")
public class PrescriptionVerificationResponse {
    @Schema(description = "Verification record identifier.", example = "123")
    private Long id;
    @Schema(description = "Verification status.", example = "PENDING_VERIFICATION")
    private String status;
    @Schema(description = "Result message.", example = "Prescription uploaded successfully")
    private String message;
}
