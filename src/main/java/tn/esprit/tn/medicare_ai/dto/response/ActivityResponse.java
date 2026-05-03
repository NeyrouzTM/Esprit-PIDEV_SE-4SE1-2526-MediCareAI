package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityResponse {
    private Long id;
    private String type;
    private Integer duration;
    private String benefit;
    private Long pregnancyTrackingId;
}
