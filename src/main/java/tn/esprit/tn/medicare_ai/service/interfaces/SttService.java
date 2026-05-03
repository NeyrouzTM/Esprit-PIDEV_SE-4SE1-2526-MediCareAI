package tn.esprit.tn.medicare_ai.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service de transcription audio (Speech-to-Text).
 * Implémentation par défaut : Whisper via l'API OpenAI.
 */
public interface SttService {

    /**
     * Transcrit un fichier audio en texte.
     *
     * @param audioFile fichier audio (webm, mp4, wav, mp3…)
     * @param language  code langue BCP-47 optionnel (ex: "fr", "en"). Peut être null.
     * @return le texte transcrit, ou une chaîne vide si la transcription échoue
     */
    String transcribe(MultipartFile audioFile, String language);
}
