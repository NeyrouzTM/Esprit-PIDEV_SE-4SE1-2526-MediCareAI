package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationSessionResponseDTO {

    private Long id;
    private String title;
    private Long creatorId;
    private String creatorName;
    private Set<Long> participantIds;
    private int documentCount;
    private LocalDateTime createdAt;
}
