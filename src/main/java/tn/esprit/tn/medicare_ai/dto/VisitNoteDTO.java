package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitNoteDTO {
    private Long medicalRecordId;
    private Long doctorId;
    private LocalDateTime visitDate;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private boolean finalized;
}