package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.request.*;
import tn.esprit.tn.medicare_ai.dto.response.MeetingLiveResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingPvResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.SttResponse;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@Tag(name = "Meetings", description = "Gestion des réunions médicales (CRUD, cycle de vie, live, PV IA)")
public class MeetingController {

    private final MeetingService meetingService;
    private final UserRepository userRepository;

    public MeetingController(MeetingService meetingService, UserRepository userRepository) {
        this.meetingService = meetingService;
        this.userRepository = userRepository;
    }

    // ── CRUD de base ─────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Créer une réunion", description = "Seuls les médecins peuvent créer une réunion")
    public ResponseEntity<MeetingResponseDTO> createMeeting(@Valid @RequestBody MeetingRequestDTO dto) {
        User user = getCurrentUser();
        return ResponseEntity.status(201).body(meetingService.createMeeting(dto, user.getId()));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réunions")
    public ResponseEntity<List<MeetingResponseDTO>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réunion par ID")
    public ResponseEntity<MeetingResponseDTO> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une réunion (organisateur uniquement)")
    public ResponseEntity<MeetingResponseDTO> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody MeetingRequestDTO dto) {
        User user = getCurrentUser();
        return ResponseEntity.ok(meetingService.updateMeeting(id, dto, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réunion (organisateur uniquement)")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        User user = getCurrentUser();
        meetingService.deleteMeeting(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ── Gestion des participants ──────────────────────────────────────────────

    @PostMapping("/{id}/participants/{userId}")
    @Operation(summary = "Ajouter un participant à la réunion")
    public ResponseEntity<MeetingResponseDTO> addParticipant(
            @PathVariable Long id,
            @PathVariable Long userId) {
        User requester = getCurrentUser();
        return ResponseEntity.ok(meetingService.addParticipant(id, userId, requester.getId()));
    }

    @DeleteMapping("/{id}/participants/{userId}")
    @Operation(summary = "Retirer un participant de la réunion")
    public ResponseEntity<MeetingResponseDTO> removeParticipant(
            @PathVariable Long id,
            @PathVariable Long userId) {
        User requester = getCurrentUser();
        return ResponseEntity.ok(meetingService.removeParticipant(id, userId, requester.getId()));
    }

    // ── Cycle de vie ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/start")
    @Operation(
        summary = "Démarrer une réunion",
        description = "Passe le statut à LIVE et enregistre l'heure de début. "
                    + "Optionnellement, fournir un lien JitSI dans le corps."
    )
    public ResponseEntity<MeetingResponseDTO> startMeeting(
            @PathVariable Long id) {

        User user = getCurrentUser();
        return ResponseEntity.ok(meetingService.startMeeting(id, null, user.getId()));
    }

    @PostMapping("/{id}/end")
    @Operation(
        summary = "Terminer une réunion",
        description = "Passe le statut à FINISHED et enregistre l'heure de fin. Le PV peut ensuite être généré."
    )
    public ResponseEntity<MeetingResponseDTO> endMeeting(@PathVariable Long id) {
        User user = getCurrentUser();
        return ResponseEntity.ok(meetingService.endMeeting(id, user.getId()));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une réunion")
    public ResponseEntity<MeetingResponseDTO> cancelMeeting(@PathVariable Long id) {
        User user = getCurrentUser();
        return ResponseEntity.ok(meetingService.cancelMeeting(id, user.getId()));
    }

    // ── Live ─────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/live")
    @Operation(
        summary = "Données de la réunion en direct",
        description = "Endpoint consommé par la page Angular /collaboration/dashboard/meetings/{id}/live. "
                    + "Retourne le lien JitSI, les participants, l'ordre du jour et les notes en cours."
    )
    public ResponseEntity<MeetingLiveResponseDTO> getLiveMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getLiveMeeting(id));
    }

    @PatchMapping("/{id}/notes")
    @Operation(
        summary = "Mettre à jour les notes de la réunion",
        description = "Sauvegarde les notes prises en temps réel pendant la réunion (organisateur ou participant)."
    )
    public ResponseEntity<MeetingResponseDTO> updateNotes(
            @PathVariable Long id,
            @RequestBody UpdateMeetingNotesRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(meetingService.updateNotes(id, request, user.getId()));
    }

    // ── PV ───────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/pv")
    @Operation(
        summary = "Récupérer le PV de la réunion",
        description = "Endpoint consommé par la page Angular /collaboration/dashboard/meetings/{id}/pv. "
                    + "Retourne le PV existant (généré ou non par l'IA)."
    )
    public ResponseEntity<MeetingPvResponseDTO> getPv(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getPv(id));
    }

    @PostMapping("/{id}/pv/generate")
    @Operation(
        summary = "Générer le PV par IA",
        description = "Génère un procès-verbal structuré via OpenAI GPT à partir des notes et de l'ordre du jour. "
                    + "La réunion doit être dans l'état FINISHED. "
                    + "Si la clé OpenAI n'est pas configurée, un PV de base est généré localement."
    )
    public ResponseEntity<MeetingPvResponseDTO> generatePv(
            @PathVariable Long id,
            @RequestBody(required = false) GeneratePvRequest request) {
        User user = getCurrentUser();
        String instructions = (request != null) ? request.getAdditionalInstructions() : null;
        return ResponseEntity.ok(meetingService.generatePv(id, instructions, user.getId()));
    }

    // ── STT ──────────────────────────────────────────────────────────────────

    @PostMapping(value = "/{id}/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Transcrire un segment audio (STT)",
        description = "Reçoit un blob audio (webm/wav) envoyé par le frontend toutes les ~8 secondes. "
                    + "Transcrit via Whisper (OpenAI) et accumule le texte dans la réunion. "
                    + "Retourne le texte du segment et la transcription complète. "
                    + "La réunion doit être en état LIVE."
    )
    public ResponseEntity<SttResponse> transcribeAudio(
            @PathVariable Long id,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestParam(value = "language", required = false) String language) {
        return ResponseEntity.ok(meetingService.transcribeAudio(id, audioFile, language));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + email));
    }
}
