package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDTO {

    private Long id;
    private Long userId;
    private Long planId;
    private String planName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private boolean activeNow;
}
