package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MedicineDetailResponse", description = "Detailed medicine view including interaction alerts.")
public class MedicineDetailResponse extends MedicineResponse {
    @Schema(description = "Manufacturer name.", example = "Sanofi")
    private String manufacturer;
    @Schema(description = "Detailed medicine description.", example = "Pain reliever and fever reducer.")
    private String description;

    @Schema(description = "Known interaction alerts for this medicine.")
    private List<DrugInteractionAlertDto> interactionAlerts = List.of();

    @Builder(builderMethodName = "medicineDetailBuilder")
    public MedicineDetailResponse(Long id,
                                  String name,
                                  String genericName,
                                  String dosageForm,
                                  String strength,
                                  Double price,
                                  Boolean prescriptionRequired,
                                  String imageUrl,
                                  String manufacturer,
                                  String description,
                                  List<DrugInteractionAlertDto> interactionAlerts) {
        super(id, name, genericName, dosageForm, strength, price, prescriptionRequired, imageUrl);
        this.manufacturer = manufacturer;
        this.description = description;
        this.interactionAlerts = interactionAlerts != null ? interactionAlerts : List.of();
    }
}
