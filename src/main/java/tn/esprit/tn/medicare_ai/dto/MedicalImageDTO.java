package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImageDTO {
    private Long medicalRecordId;
    private String imageType;
    private String imageUrl;
    private LocalDate uploadDate;
    private String description;
}