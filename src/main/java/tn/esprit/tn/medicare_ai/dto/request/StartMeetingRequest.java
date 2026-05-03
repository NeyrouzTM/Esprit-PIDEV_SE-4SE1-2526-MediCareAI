package tn.esprit.tn.medicare_ai.dto.request;

import lombok.*;

/**
 * Corps optionnel pour POST /api/meetings/{id}/start
 * Permet de passer un lien de réunion si non défini à la création.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartMeetingRequest {

    /** Lien JitSI / WebRTC à utiliser pour cette session */
    private String meetingLink;
}
