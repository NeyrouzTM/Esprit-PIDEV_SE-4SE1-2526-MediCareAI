package tn.esprit.tn.medicare_ai.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.PrescriptionStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PrescriptionResponse", description = "Summary view of a prescription.")
public class PrescriptionResponse {
    @Schema(description = "Prescription identifier.", example = "100")
    private Long id;
    @Schema(description = "Patient identifier.", example = "25")
    private Long patientId;
    @Schema(description = "Patient full name.", example = "Sara Ben Ali")
    private String patientName;
    @Schema(description = "Doctor identifier.", example = "7")
    private Long doctorId;
    @Schema(description = "Doctor full name.", example = "Dr. Mourad Trabelsi")
    private String doctorName;

    @Schema(description = "Prescription issue date.", example = "2026-03-20")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @Schema(description = "Prescription expiry date.", example = "2026-04-20")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Schema(description = "Prescription status.", example = "ACTIVE")
    private PrescriptionStatus status;
    @Schema(description = "Total item count in this prescription.", example = "2")
    private Integer itemCount;
}
