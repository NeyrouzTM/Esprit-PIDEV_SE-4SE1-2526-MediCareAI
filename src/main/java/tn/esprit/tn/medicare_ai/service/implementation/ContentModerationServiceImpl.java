package tn.esprit.tn.medicare_ai.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.tn.medicare_ai.service.interfaces.ContentModerationService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Modération du contenu — deux niveaux :
 *
 * 1) API Ninjas (si clé configurée) — liste très complète
 * 2) Filtre local intégré (fallback) — couvre EN/FR/AR
 */
@Service
@Slf4j
public class ContentModerationServiceImpl implements ContentModerationService {

    @Value("${apininjas.api.key:}")
    private String apiNinjasKey;

    private static final String API_NINJAS_URL = "https://api.api-ninjas.com/v1/profanityfilter";

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Liste locale de bad words (EN + FR + AR translittéré) ────────────────
    private static final List<String> BAD_WORDS = Arrays.asList(
        // Anglais — insultes légères
        "stupid", "idiot", "dumb", "moron", "fool", "loser", "jerk", "ugly",
        "retard", "imbecile", "cretin", "dimwit", "nitwit", "halfwit", "dunce",
        "airhead", "blockhead", "bonehead", "numbskull", "knucklehead",
        // Anglais — grossièretés
        "fuck", "shit", "bitch", "asshole", "bastard", "crap", "damn", "hell",
        "piss", "cock", "dick", "pussy", "cunt", "whore", "slut", "fag",
        "nigger", "nigga", "faggot", "prick", "wanker", "twat", "bollocks",
        // Anglais — haine
        "hate you", "kill yourself", "go die", "kys", "shut up", "shut the fuck",
        // Français — insultes légères
        "idiot", "idiote", "stupide", "imbecile", "cretin", "cretine",
        "abruti", "abrutie", "nul", "nulle", "debile", "attarde", "attardee",
        "bouffon", "bouffonne", "minus", "navet", "boulet",
        // Français — grossièretés
        "merde", "putain", "connard", "connasse", "salaud", "salope",
        "enculer", "encule", "niquer", "nique", "fdp", "tg", "va te faire",
        "ta gueule", "ferme ta gueule", "batard", "batarde",
        "con", "conne", "couille", "bite", "chier", "branler"

    );

    // Patterns compilés une seule fois pour la performance
    private static final List<Pattern> BAD_PATTERNS = new ArrayList<>();
    static {
        for (String word : BAD_WORDS) {
            // \b pour les mots entiers, case-insensitive
            try {
                BAD_PATTERNS.add(Pattern.compile(
                    "\\b" + Pattern.quote(word) + "\\b",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                ));
            } catch (Exception ignored) {}
        }
    }

    // ── Interface publique ────────────────────────────────────────────────────

    @Override
    public void checkContent(String text) {
        if (!isClean(text)) {
            throw new IllegalArgumentException(
                "Votre message contient des mots inappropriés et ne peut pas être publié."
            );
        }
    }

    @Override
    public boolean isClean(String text) {
        if (text == null || text.isBlank()) return true;

        // Priorité : API Ninjas si clé configurée
        if (apiNinjasKey != null && !apiNinjasKey.isBlank()) {
            try {
                return isCleanApiNinjas(text);
            } catch (Exception e) {
                log.error("[Modération] API Ninjas erreur : {} — fallback local", e.getMessage());
            }
        }

        // Fallback : filtre local
        return isCleanLocal(text);
    }

    @Override
    public String getCensoredText(String text) {
        if (text == null || text.isBlank()) return text;

        if (apiNinjasKey != null && !apiNinjasKey.isBlank()) {
            try {
                return getCensoredApiNinjas(text);
            } catch (Exception e) {
                log.error("[Modération] API Ninjas getCensored erreur : {} — fallback local", e.getMessage());
            }
        }

        return getCensoredLocal(text);
    }

    // ── Filtre local ──────────────────────────────────────────────────────────

    private boolean isCleanLocal(String text) {
        String normalized = normalize(text);
        for (Pattern pattern : BAD_PATTERNS) {
            if (pattern.matcher(normalized).find()) {
                log.warn("[Modération locale] Mot interdit détecté dans : '{}'", text);
                return false;
            }
        }
        return true;
    }

    private String getCensoredLocal(String text) {
        String result = text;
        String normalized = normalize(text);
        for (int i = 0; i < BAD_PATTERNS.size(); i++) {
            if (BAD_PATTERNS.get(i).matcher(normalized).find()) {
                String word = BAD_WORDS.get(i);
                String stars = "*".repeat(word.length());
                result = BAD_PATTERNS.get(i).matcher(result).replaceAll(stars);
            }
        }
        return result;
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replace("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e")
                .replace("à", "a").replace("â", "a").replace("ä", "a")
                .replace("ù", "u").replace("û", "u").replace("ü", "u")
                .replace("î", "i").replace("ï", "i")
                .replace("ô", "o").replace("ö", "o")
                .replace("ç", "c")
                .replace("0", "o").replace("3", "e").replace("1", "i").replace("@", "a");
    }

    // ── API Ninjas ────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private boolean isCleanApiNinjas(String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = API_NINJAS_URL + "?text=" + encoded;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiNinjasKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Boolean hasProfanity = (Boolean) response.getBody().get("has_profanity");
            if (Boolean.TRUE.equals(hasProfanity)) {
                log.warn("[Modération API Ninjas] Contenu bloqué : '{}'", text);
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private String getCensoredApiNinjas(String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = API_NINJAS_URL + "?text=" + encoded;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiNinjasKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String censored = (String) response.getBody().get("censored");
            return censored != null ? censored : text;
        }
        return text;
    }
}
