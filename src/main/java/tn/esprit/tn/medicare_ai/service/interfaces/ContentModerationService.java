package tn.esprit.tn.medicare_ai.service.interfaces;

/**
 * Service de modération du contenu via PurgoMalum (API externe gratuite, sans clé).
 */
public interface ContentModerationService {

    /**
     * Vérifie si le texte est propre.
     * Lève IllegalArgumentException si bad words détectés.
     */
    void checkContent(String text);

    /**
     * Retourne true si le texte est propre, false sinon.
     */
    boolean isClean(String text);

    /**
     * Retourne le texte avec les bad words remplacés par ****.
     */
    String getCensoredText(String text);
}
