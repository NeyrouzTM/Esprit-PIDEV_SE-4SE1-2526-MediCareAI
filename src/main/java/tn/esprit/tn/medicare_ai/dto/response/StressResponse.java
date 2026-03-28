package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StressResponse {
    private Long id;
    private Integer level;
    private String message;
    private LocalDate date;
    private Long userId;
}
