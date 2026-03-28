package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationScheduleResponse {
    private Long id;
    private String medicineName;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate recordDate;
    private Long userId;
}
