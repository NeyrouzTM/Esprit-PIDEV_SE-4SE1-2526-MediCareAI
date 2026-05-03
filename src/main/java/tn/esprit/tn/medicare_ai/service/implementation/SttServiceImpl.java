package tn.esprit.tn.medicare_ai.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.service.interfaces.SttService;

import java.util.Map;

/**
 * Implémentation STT via l'API Whisper d'OpenAI.
 *
 * Endpoint utilisé : POST https://api.openai.com/v1/audio/transcriptions
 * Format attendu   : multipart/form-data  (file + model)
 *
 * Si la clé OpenAI n'est pas configurée, retourne une chaîne vide
 * (le frontend utilisera alors sa propre Web Speech API comme fallback).
 *
 * Aucune dépendance SDK supplémentaire — utilise uniquement RestTemplate
 * pour rester compatible avec le pom.xml actuel.
 */
@Service
public class SttServiceImpl implements SttService {

    private static final Logger log = LoggerFactory.getLogger(SttServiceImpl.class);

    /** Modèle Whisper à utiliser (whisper-1 est le seul disponible via l'API REST OpenAI) */
    private static final String WHISPER_MODEL = "whisper-1";

    private static final String WHISPER_URL = "https://api.openai.com/v1/audio/transcriptions";

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String transcribe(MultipartFile audioFile, String language) {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("STT : clé OpenAI non configurée → transcription ignorée (le frontend utilisera Web Speech API)");
            return "";
        }

        if (audioFile == null || audioFile.isEmpty()) {
            log.warn("STT : fichier audio vide reçu");
            return "";
        }

        try {
            return callWhisper(audioFile, language);
        } catch (Exception e) {
            log.error("STT : erreur lors de l'appel Whisper : {}", e.getMessage());
            return "";
        }
    }

    // ── Appel HTTP Whisper ───────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String callWhisper(MultipartFile audioFile, String language) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openAiApiKey);

        // Détermine l'extension à partir du content-type ou du nom de fichier
        String filename = resolveFilename(audioFile);

        // Wrap les bytes dans une ressource nommée (Whisper a besoin d'une extension reconnue)
        byte[] bytes = audioFile.getBytes();
        ByteArrayResource audioResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", WHISPER_MODEL);
        body.add("file", audioResource);
        body.add("response_format", "text"); // retourne directement le texte brut

        // Langue optionnelle (améliore la précision si connue)
        if (language != null && !language.isBlank()) {
            body.add("language", language);
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // response_format=text → le corps est directement une String (pas du JSON)
        ResponseEntity<String> response = restTemplate.postForEntity(WHISPER_URL, request, String.class);

        String text = response.getBody();
        if (text == null) return "";

        // Nettoie les espaces superflus
        return text.trim();
    }

    /**
     * Résout un nom de fichier avec extension valide pour Whisper.
     * Whisper accepte : mp3, mp4, mpeg, mpga, m4a, wav, webm
     */
    private String resolveFilename(MultipartFile audioFile) {
        String original = audioFile.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return original;
        }

        String contentType = audioFile.getContentType();
        if (contentType != null) {
            return switch (contentType) {
                case "audio/webm", "video/webm"  -> "audio.webm";
                case "audio/wav", "audio/x-wav"  -> "audio.wav";
                case "audio/mpeg", "audio/mp3"   -> "audio.mp3";
                case "audio/mp4", "video/mp4"    -> "audio.mp4";
                case "audio/ogg"                 -> "audio.ogg";
                default                          -> "audio.webm";
            };
        }

        return "audio.webm"; // fallback — format produit par MediaRecorder dans Chrome/Firefox
    }
}
