package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PrescriptionRequest", description = "Request payload for doctor prescription issuance.")
public class PrescriptionRequest {
    @Schema(description = "Patient identifier receiving the prescription.", example = "25")
    @NotNull
    private Long patientId;

    @Schema(description = "Optional consultation identifier.", example = "300")
    private Long consultationId;

    @Schema(description = "Prescription medicine items.")
    @NotEmpty
    @Valid
    private List<PrescriptionItemRequest> items;

    @Schema(description = "Prescription expiry date.", example = "2026-12-31")
    @NotNull
    @FutureOrPresent
    private LocalDate expiryDate;
}
