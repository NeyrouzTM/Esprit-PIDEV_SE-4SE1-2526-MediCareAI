package tn.esprit.tn.medicare_ai.dto.request;

import lombok.*;

/**
 * Corps optionnel pour POST /api/meetings/{id}/pv/generate
 * Permet de forcer la régénération du PV ou d'ajouter des instructions à l'IA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratePvRequest {

    /** Instructions supplémentaires pour l'IA (ex: "Insiste sur les décisions prises") */
    private String additionalInstructions;

    /** Si true, régénère même si un PV existe déjà */
    private boolean forceRegenerate = false;
}
