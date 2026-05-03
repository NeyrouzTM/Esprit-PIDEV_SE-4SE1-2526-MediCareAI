package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepResponse {
    private Long id;
    private Float hours;
    private Integer quality;
    private LocalDate date;
    private Long userId;
}
