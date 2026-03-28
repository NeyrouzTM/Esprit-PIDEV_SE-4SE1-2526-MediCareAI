package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private Long medicalRecordId;
    private Long doctorId;
    private String medicationName;
    private String dosage;
    private String duration;
    private String instructions;
    private LocalDate prescriptionDate;
    private boolean active;
}