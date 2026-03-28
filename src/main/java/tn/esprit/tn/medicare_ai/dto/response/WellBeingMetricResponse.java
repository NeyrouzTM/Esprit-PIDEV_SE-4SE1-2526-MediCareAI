package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WellBeingMetricResponse {
    private Long id;
    private String level;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;
}
