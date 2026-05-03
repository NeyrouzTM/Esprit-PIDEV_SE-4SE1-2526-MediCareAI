package tn.esprit.tn.medicare_ai.dto.request;

import lombok.*;

/**
 * Corps pour PATCH /api/meetings/{id}/notes
 * Permet de sauvegarder les notes prises pendant la réunion en direct.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMeetingNotesRequest {

    private String meetingNotes;
}
