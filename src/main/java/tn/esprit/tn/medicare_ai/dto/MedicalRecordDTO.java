package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDTO {
    private Long patientId;
    private String bloodType;
    private Double height;
    private Double weight;
    private LocalDate dateOfBirth;
    private String medicalHistory;
    private String chronicDiseases;
}