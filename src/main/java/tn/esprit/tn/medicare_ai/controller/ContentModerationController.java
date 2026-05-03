package tn.esprit.tn.medicare_ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.service.interfaces.ContentModerationService;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Endpoint de modération consommé par Angular.
 *
 * POST /api/forum/check-content
 * Body : { "text": "..." }  OU  { "anyKey": "...", "anyKey2": "..." }
 *
 * Réponse :
 * {
 *   "clean": true/false,
 *   "message": "...",
 *   "censored": "texte avec **** à la place des bad words"
 * }
 */
@RestController
@RequestMapping("/api/forum")
@Slf4j
public class ContentModerationController {

    private final ContentModerationService moderationService;

    public ContentModerationController(ContentModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/check-content")
    public ResponseEntity<Map<String, Object>> checkContent(
            @RequestBody Map<String, String> body) {

        // Accepte { "text": "..." } ou n'importe quelle structure — on concatène tout
        String text = body.containsKey("text")
                ? body.get("text")
                : body.values().stream()
                        .filter(v -> v != null && !v.isBlank())
                        .collect(Collectors.joining(" "));

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "clean", true,
                    "message", "Texte vide",
                    "censored", ""
            ));
        }

        try {
            boolean clean = moderationService.isClean(text);

            if (clean) {
                return ResponseEntity.ok(Map.of(
                        "clean", true,
                        "message", "✅ Contenu approuvé",
                        "censored", text
                ));
            } else {
                String censored = moderationService.getCensoredText(text);
                return ResponseEntity.ok(Map.of(
                        "clean", false,
                        "message", "❌ Votre message contient des mots inappropriés et ne peut pas être publié.",
                        "censored", censored != null ? censored : text
                ));
            }

        } catch (IllegalStateException e) {
            // Service de modération indisponible
            return ResponseEntity.status(503).body(Map.of(
                    "clean", false,
                    "message", e.getMessage(),
                    "censored", text
            ));
        }
    }
}
