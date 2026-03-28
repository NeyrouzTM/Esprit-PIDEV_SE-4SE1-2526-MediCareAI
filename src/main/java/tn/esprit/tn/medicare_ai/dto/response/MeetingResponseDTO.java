package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingResponseDTO {

    private Long id;
    private String title;
    private LocalDateTime dateTime;
    private String meetingLink;
    private Long organizerId;
    private String organizerName;
    private Set<Long> participantIds;
    private boolean recorded;
    private String recordingUrl;
}
