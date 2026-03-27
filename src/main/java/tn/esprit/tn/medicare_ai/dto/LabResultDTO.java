package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultDTO {
    private Long medicalRecordId;
    private String testName;
    private String result;
    private String unit;
    private String normalRange;
    private LocalDate testDate;
    private String notes;
}