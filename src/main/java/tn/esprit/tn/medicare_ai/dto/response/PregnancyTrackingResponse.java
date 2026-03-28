package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyTrackingResponse {
    private Long id;
    private LocalDate startDate;
    private Integer currentWeek;
    private String notes;
    private LocalDate dueDate;
    private Long userId;
}
