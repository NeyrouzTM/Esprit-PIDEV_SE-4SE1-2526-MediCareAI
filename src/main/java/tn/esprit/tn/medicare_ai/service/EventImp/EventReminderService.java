package tn.esprit.tn.medicare_ai.service.EventImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO.ReminderResult;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventReminderService {

    private final HealthEventRepository healthEventRepository;

    // anti-doublon in-memory (eventId:email). Reset on app restart.
    private final Set<String> sentReminderKeys = ConcurrentHashMap.newKeySet();

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Scheduler: exécute toutes les heures et envoie les reminders automatiquement.
     * Il appelle la même logique que runRemindersNow().
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void scheduledSend() {
        runRemindersNow(); // on ignore le résultat (logs + envois)
    }

    /**
     * Expose la liste des reminders envoyés (ou qui seraient envoyés) pour la fenêtre <= 24h.
     * Retourne la liste des ReminderResult afin que le controller puisse les renvoyer en JSON.
     */
    public List<ReminderResult> runRemindersNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(24);

        // Récupère events dont now < eventDate <= limit
        List<HealthEvent> events = healthEventRepository.findEventsForReminder(now, limit);
        List<ReminderResult> results = new ArrayList<>();

        if (events == null || events.isEmpty()) {
            log.info("[REMINDER] Aucun événement <= 24h à rappeler.");
            // nettoie les clés obsolètes pour limiter la mémoire
            cleanupExpiredKeys(now);
            return results;
        }

        for (HealthEvent event : events) {
            if (event == null || event.getEventDate() == null || event.getParticipants() == null) {
                continue;
            }

            String eventTime = event.getEventDate().format(TIME_FMT);
            String title = event.getTitle() != null ? event.getTitle() : "Événement";
            String locationPart = (event.getLocation() != null && !event.getLocation().isBlank())
                    ? " à " + event.getLocation() : "";

            String message = "Tu as un event demain à " + eventTime + " : " + title + locationPart;

            for (User participant : event.getParticipants()) {
                if (participant == null) continue;
                String email = participant.getEmail();
                if (email == null || email.isBlank()) continue;

                String key = event.getId() + ":" + email.trim().toLowerCase();
                if (sentReminderKeys.contains(key)) {
                    // déjà envoyé -> skip
                    continue;
                }

                // Ici tu peux brancher l'envoi réel (email, push, websocket...) ; pour l'instant on log et on collecte.
                log.info("[EVENT REMINDER] To: {} | Message: {}", email, message);

                ReminderResult r = new ReminderResult(
                        event.getId(),
                        title,
                        event.getEventDate(),
                        email,
                        message
                );
                results.add(r);

                // marque comme envoyé (empêche doublons)
                sentReminderKeys.add(key);
            }
        }

        // Nettoyage pour éviter croissance infinie
        cleanupExpiredKeys(now);
        return results;
    }

    /**
     * Nettoie sentReminderKeys en ne gardant que les clés correspondant à événements encore futurs.
     * Ceci évite la croissance infinie de la structure in-memory.
     */
    private void cleanupExpiredKeys(LocalDateTime now) {
        try {
            List<HealthEvent> all = healthEventRepository.findAll();
            Set<String> valid = ConcurrentHashMap.newKeySet();
            if (all != null) {
                for (HealthEvent e : all) {
                    if (e == null || e.getEventDate() == null || !e.getEventDate().isAfter(now)) continue;
                    if (e.getParticipants() == null) continue;
                    for (User p : e.getParticipants()) {
                        if (p == null || p.getEmail() == null || p.getEmail().isBlank()) continue;
                        valid.add(e.getId() + ":" + p.getEmail().trim().toLowerCase());
                    }
                }
            }
            sentReminderKeys.retainAll(valid);
        } catch (Exception ex) {
            log.warn("[REMINDER] cleanupExpiredKeys failed", ex);
        }
    }
}
