package tn.esprit.tn.medicare_ai.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.request.StartMeetingRequest;
import tn.esprit.tn.medicare_ai.dto.request.UpdateMeetingNotesRequest;
import tn.esprit.tn.medicare_ai.dto.response.MeetingLiveResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingPvResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.SttResponse;

import java.util.List;

public interface MeetingService {

    // ── CRUD de base ─────────────────────────────────────────────────────────
    MeetingResponseDTO createMeeting(MeetingRequestDTO dto, Long organizerId);
    List<MeetingResponseDTO> getAllMeetings();
    MeetingResponseDTO getMeetingById(Long id);
    MeetingResponseDTO updateMeeting(Long id, MeetingRequestDTO dto, Long organizerId);
    void deleteMeeting(Long id, Long organizerId);

    // ── Gestion des participants ──────────────────────────────────────────────
    MeetingResponseDTO addParticipant(Long meetingId, Long userId, Long requesterId);
    MeetingResponseDTO removeParticipant(Long meetingId, Long userId, Long requesterId);

    // ── Cycle de vie ─────────────────────────────────────────────────────────
    /** Démarre la réunion → status LIVE, startedAt = now */
    MeetingResponseDTO startMeeting(Long id, StartMeetingRequest request, Long requesterId);

    /** Termine la réunion → status FINISHED, endedAt = now */
    MeetingResponseDTO endMeeting(Long id, Long requesterId);

    /** Annule la réunion → status CANCELLED */
    MeetingResponseDTO cancelMeeting(Long id, Long requesterId);

    // ── Live ─────────────────────────────────────────────────────────────────
    /** Données complètes pour la page /live */
    MeetingLiveResponseDTO getLiveMeeting(Long id);

    /** Sauvegarde les notes prises pendant la réunion */
    MeetingResponseDTO updateNotes(Long id, UpdateMeetingNotesRequest request, Long requesterId);

    // ── PV ───────────────────────────────────────────────────────────────────
    /** Génère le PV via IA et le persiste */
    MeetingPvResponseDTO generatePv(Long id, String additionalInstructions, Long requesterId);

    /** Récupère le PV existant (sans régénérer) */
    MeetingPvResponseDTO getPv(Long id);

    // ── STT ──────────────────────────────────────────────────────────────────
    /**
     * Transcrit un segment audio et l'accumule dans la transcription de la réunion.
     *
     * @param id        identifiant de la réunion
     * @param audioFile segment audio envoyé par le frontend (webm, wav…)
     * @param language  code langue optionnel (ex: "fr")
     * @return le texte du segment + la transcription complète accumulée
     */
    SttResponse transcribeAudio(Long id, MultipartFile audioFile, String language);
}
