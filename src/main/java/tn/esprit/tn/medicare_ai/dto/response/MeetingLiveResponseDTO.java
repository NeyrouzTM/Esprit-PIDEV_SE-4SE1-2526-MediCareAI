package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;
import tn.esprit.tn.medicare_ai.entity.MeetingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO renvoyé par GET /api/meetings/{id}/live
 * Contient toutes les données nécessaires à la page de réunion en direct.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingLiveResponseDTO {

    private Long id;
    private String title;
    private MeetingStatus status;
    private String meetingLink;

    private Long organizerId;
    private String organizerName;

    /** Participants avec leur nom pour l'affichage */
    private Set<ParticipantInfo> participants;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;

    private List<String> agendaPoints;

    /** Notes en temps réel (modifiables pendant la réunion) */
    private String meetingNotes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParticipantInfo {
        private Long id;
        private String fullName;
        private String email;
    }
}
