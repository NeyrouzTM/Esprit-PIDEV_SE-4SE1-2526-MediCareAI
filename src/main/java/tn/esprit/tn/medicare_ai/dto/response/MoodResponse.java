package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MoodResponse {
    private Long id;
    private Integer level;
    private String note;
    private LocalDate date;
    private Long userId;
}
