package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import tn.esprit.tn.medicare_ai.entity.MeetingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO renvoyé par GET /api/meetings/{id}/pv
 * Contient le procès-verbal généré par l'IA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingPvResponseDTO {

    private Long meetingId;
    private String title;
    private MeetingStatus status;

    private String organizerName;
    private Set<String> participantNames;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private List<String> agendaPoints;

    /** Notes brutes saisies pendant la réunion */
    private String meetingNotes;

    /** PV généré par l'IA (ou rédigé manuellement) */
    private String pvContent;

    /** true si le PV a été généré par l'IA */
    private Boolean pvGenerated;
}
