package tn.esprit.tn.medicare_ai.service.EventImp;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.entity.HealthEvent;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.event.EventRecommendationRepository;
import tn.esprit.tn.medicare_ai.repository.event.HealthEventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecommendationService {

    private final EventRecommendationRepository eventRecommendationRepository;
    private final HealthEventRepository healthEventRepository;

    /**
     * Retourne les événements triés par catégories préférées de l'utilisateur
     * (catégories auxquelles il a participé le plus en premier)
     */
    public List<HealthEvent> getRecommendedEvents(Long userId) {
        User user = eventRecommendationRepository.findUserWithEvents(userId);

        if (user == null || user.getEvents() == null || user.getEvents().isEmpty()) {
            log.info("[Recommendation] User {} has no event history. Returning all events.", userId);
            return healthEventRepository.findAll();
        }

        // Récupère l'ordre des catégories (par fréquence décroissante)
        List<Object[]> categoryFrequency = eventRecommendationRepository.getCategoryFrequencyForUser(userId);
        List<Object> categoryOrder = new ArrayList<>();

        for (Object[] row : categoryFrequency) {
            categoryOrder.add(row[0]); // la catégorie
        }

        log.info("[Recommendation] Category order for userId={}: {}", userId, categoryOrder);

        // Récupère tous les événements
        List<HealthEvent> allEvents = healthEventRepository.findAll();

        // Trie les événements par catégorie (ordre des préférences en premier, puis autres)
        return allEvents.stream()
                .sorted((e1, e2) -> {
                    Object cat1 = e1.getCategory();
                    Object cat2 = e2.getCategory();

                    int idx1 = categoryOrder.indexOf(cat1);
                    int idx2 = categoryOrder.indexOf(cat2);

                    // Si catégorie dans l'historique, la mettre en avant
                    if (idx1 != -1 && idx2 != -1) return Integer.compare(idx1, idx2);
                    if (idx1 != -1) return -1; // e1 prioritaire
                    if (idx2 != -1) return 1;  // e2 prioritaire

                    // Sinon, tri alphabétique
                    return cat1.toString().compareTo(cat2.toString());
                })
                .collect(Collectors.toList());
    }

    /**
     * Retourne les événements groupés par catégorie (pour un rendu UI en listes/sections)
     */
    public Map<String, List<HealthEvent>> getRecommendedEventsGrouped(Long userId) {
        List<HealthEvent> recommended = getRecommendedEvents(userId);
        return recommended.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() != null ? e.getCategory().toString() : "OTHER",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}
