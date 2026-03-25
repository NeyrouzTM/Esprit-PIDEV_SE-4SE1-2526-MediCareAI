package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PrescriptionDetailResponse", description = "Detailed prescription view including all items.")
public class PrescriptionDetailResponse extends PrescriptionResponse {
    @Schema(description = "Prescription items.")
    private List<PrescriptionItemResponse> items = List.of();

    @Builder(builderMethodName = "prescriptionDetailBuilder")
    public PrescriptionDetailResponse(Long id,
                                      Long patientId,
                                      String patientName,
                                      Long doctorId,
                                      String doctorName,
                                      LocalDate issueDate,
                                      LocalDate expiryDate,
                                      tn.esprit.tn.medicare_ai.entity.PrescriptionStatus status,
                                      Integer itemCount,
                                      List<PrescriptionItemResponse> items) {
        super(id, patientId, patientName, doctorId, doctorName, issueDate, expiryDate, status, itemCount);
        this.items = items != null ? items : List.of();
    }
}
