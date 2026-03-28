package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyCheckupResponse {
    private Long id;
    private LocalDate date;
    private String observation;
    private Double weightKg;
    private String symptoms;
    private Integer fetalMovements;
    private Long pregnancyTrackingId;
}
