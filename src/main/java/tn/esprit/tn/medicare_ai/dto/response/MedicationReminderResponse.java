package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationReminderResponse {
    private Long id;
    private LocalTime time;
    private String message;
    private Long medicationScheduleId;
}
