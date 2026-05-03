package tn.esprit.tn.medicare_ai.dto.response;

import lombok.*;

/**
 * Réponse de l'endpoint POST /api/meetings/{id}/stt
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SttResponse {

    /** Texte transcrit pour ce segment audio */
    private String text;

    /** Transcription complète accumulée depuis le début de la réunion */
    private String fullTranscription;
}
