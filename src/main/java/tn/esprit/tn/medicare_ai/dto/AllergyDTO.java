package tn.esprit.tn.medicare_ai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergyDTO {
    private Long medicalRecordId;
    private String allergyName;
    private String severity;
    private String reaction;
    private String notes;
}