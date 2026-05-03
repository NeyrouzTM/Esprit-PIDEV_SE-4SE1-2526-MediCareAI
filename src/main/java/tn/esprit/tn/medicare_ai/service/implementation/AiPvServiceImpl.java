package tn.esprit.tn.medicare_ai.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.tn.medicare_ai.entity.Meeting;
import tn.esprit.tn.medicare_ai.service.interfaces.AiPvService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation du service IA pour la génération du PV.
 *
 * Utilise l'API OpenAI (GPT-4o-mini) via un appel HTTP direct (pas de SDK externe)
 * pour rester compatible avec le pom.xml actuel.
 *
 * Si la clé API n'est pas configurée, un PV de base est généré localement
 * sans appel réseau (mode fallback).
 */
@Service
public class AiPvServiceImpl implements AiPvService {

    private static final Logger log = LoggerFactory.getLogger(AiPvServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.api.model:gpt-4o-mini}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generatePv(Meeting meeting, String additionalInstructions) {
        String prompt = buildPrompt(meeting, additionalInstructions);

        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("Clé OpenAI non configurée → génération du PV en mode local (fallback)");
            return generateFallbackPv(meeting);
        }

        try {
            return callOpenAi(prompt);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel OpenAI, utilisation du fallback : {}", e.getMessage());
            return generateFallbackPv(meeting);
        }
    }

    // ── Appel HTTP OpenAI ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String callOpenAi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Tu es un assistant médical expert en rédaction de procès-verbaux de réunions médicales. "
                                + "Tu rédiges des PV clairs, structurés et professionnels en français."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 2000,
                "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    // ── Construction du prompt ───────────────────────────────────────────────

    private String buildPrompt(Meeting meeting, String additionalInstructions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Génère un procès-verbal professionnel pour la réunion médicale suivante :\n\n");
        sb.append("**Titre :** ").append(meeting.getTitle()).append("\n");
        sb.append("**Organisateur :** ").append(meeting.getOrganizer().getFullName()).append("\n");

        if (meeting.getStartedAt() != null) {
            sb.append("**Début :** ").append(meeting.getStartedAt().format(FMT)).append("\n");
        }
        if (meeting.getEndedAt() != null) {
            sb.append("**Fin :** ").append(meeting.getEndedAt().format(FMT)).append("\n");
        }

        if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
            String participants = meeting.getParticipants().stream()
                    .map(u -> u.getFullName() + " (" + u.getRole() + ")")
                    .collect(Collectors.joining(", "));
            sb.append("**Participants :** ").append(participants).append("\n");
        }

        if (meeting.getAgendaPoints() != null && !meeting.getAgendaPoints().isBlank()) {
            sb.append("\n**Ordre du jour :**\n");
            for (String point : meeting.getAgendaPoints().split("\n")) {
                sb.append("- ").append(point.trim()).append("\n");
            }
        }

        if (meeting.getMeetingNotes() != null && !meeting.getMeetingNotes().isBlank()) {
            sb.append("\n**Notes de la réunion :**\n").append(meeting.getMeetingNotes()).append("\n");
        }

        if (meeting.getTranscription() != null && !meeting.getTranscription().isBlank()) {
            sb.append("\n**Transcription audio (STT) :**\n").append(meeting.getTranscription()).append("\n");
        }

        if (additionalInstructions != null && !additionalInstructions.isBlank()) {
            sb.append("\n**Instructions supplémentaires :** ").append(additionalInstructions).append("\n");
        }

        sb.append("\nLe PV doit inclure : en-tête, liste des présents, résumé des points abordés, "
                + "décisions prises, actions à suivre, et signature de clôture.");

        return sb.toString();
    }

    // ── Fallback sans IA ─────────────────────────────────────────────────────

    private String generateFallbackPv(Meeting meeting) {
        StringBuilder pv = new StringBuilder();
        pv.append("═══════════════════════════════════════════════════════\n");
        pv.append("           PROCÈS-VERBAL DE RÉUNION MÉDICALE\n");
        pv.append("═══════════════════════════════════════════════════════\n\n");

        pv.append("Titre : ").append(meeting.getTitle()).append("\n");
        pv.append("Organisateur : ").append(meeting.getOrganizer().getFullName()).append("\n");

        if (meeting.getStartedAt() != null) {
            pv.append("Date de début : ").append(meeting.getStartedAt().format(FMT)).append("\n");
        }
        if (meeting.getEndedAt() != null) {
            pv.append("Date de fin : ").append(meeting.getEndedAt().format(FMT)).append("\n");
        }

        pv.append("\n── PARTICIPANTS ──────────────────────────────────────\n");
        if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
            meeting.getParticipants().forEach(p ->
                    pv.append("  • ").append(p.getFullName()).append(" (").append(p.getRole()).append(")\n"));
        } else {
            pv.append("  Aucun participant enregistré.\n");
        }

        pv.append("\n── ORDRE DU JOUR ─────────────────────────────────────\n");
        if (meeting.getAgendaPoints() != null && !meeting.getAgendaPoints().isBlank()) {
            for (String point : meeting.getAgendaPoints().split("\n")) {
                pv.append("  • ").append(point.trim()).append("\n");
            }
        } else {
            pv.append("  Non défini.\n");
        }

        pv.append("\n── NOTES DE LA RÉUNION ───────────────────────────────\n");
        if (meeting.getMeetingNotes() != null && !meeting.getMeetingNotes().isBlank()) {
            pv.append(meeting.getMeetingNotes()).append("\n");
        } else {
            pv.append("  Aucune note enregistrée.\n");
        }

        if (meeting.getTranscription() != null && !meeting.getTranscription().isBlank()) {
            pv.append("\n── TRANSCRIPTION AUDIO ───────────────────────────────\n");
            pv.append(meeting.getTranscription()).append("\n");
        }

        pv.append("\n── CLÔTURE ───────────────────────────────────────────\n");
        pv.append("La réunion a été clôturée par ").append(meeting.getOrganizer().getFullName()).append(".\n");
        pv.append("\n[Document généré automatiquement par MediCare AI]\n");

        return pv.toString();
    }
}
