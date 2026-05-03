package tn.esprit.tn.medicare_ai.service.interfaces;

import tn.esprit.tn.medicare_ai.entity.Meeting;

/**
 * Service d'intelligence artificielle pour la génération du PV de réunion.
 */
public interface AiPvService {

    /**
     * Génère un procès-verbal structuré à partir des données de la réunion.
     *
     * @param meeting               la réunion terminée
     * @param additionalInstructions instructions supplémentaires (peut être null)
     * @return le texte du PV généré
     */
    String generatePv(Meeting meeting, String additionalInstructions);
}
