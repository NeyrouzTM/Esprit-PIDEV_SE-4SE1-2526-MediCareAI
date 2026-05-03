package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import tn.esprit.tn.medicare_ai.entity.MeetingStatus;

import java.time.LocalDateTime;
import java.util.List;
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
    private Set<String> participantNames;
    private Boolean recorded;
    private String recordingUrl;

    // Nouveaux champs cycle de vie
    private MeetingStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private List<String> agendaPoints;
    private String meetingNotes;
    private Boolean pvGenerated;
}
