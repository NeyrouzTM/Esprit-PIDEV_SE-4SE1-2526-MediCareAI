package tn.esprit.tn.medicare_ai.entity;

/**
 * Cycle de vie d'une réunion.
 * PLANNED  → réunion créée, pas encore démarrée
 * LIVE     → réunion en cours (startedAt renseigné)
 * FINISHED → réunion terminée (endedAt renseigné, PV disponible)
 * CANCELLED → réunion annulée
 */
public enum MeetingStatus {
    PLANNED,
    LIVE,
    FINISHED,
    CANCELLED
}
