package tn.esprit.tn.medicare_ai.service.implementation;

import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.request.StartMeetingRequest;
import tn.esprit.tn.medicare_ai.dto.request.UpdateMeetingNotesRequest;
import tn.esprit.tn.medicare_ai.dto.response.MeetingLiveResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingPvResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.SttResponse;
import tn.esprit.tn.medicare_ai.entity.Meeting;
import tn.esprit.tn.medicare_ai.entity.MeetingStatus;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MeetingRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.AiPvService;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;
import tn.esprit.tn.medicare_ai.service.interfaces.SttService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final AiPvService aiPvService;
    private final SttService sttService;

    public MeetingServiceImpl(MeetingRepository meetingRepository,
                               UserRepository userRepository,
                               AiPvService aiPvService,
                               SttService sttService) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.aiPvService = aiPvService;
        this.sttService = sttService;
    }

    // ── CRUD de base ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MeetingResponseDTO createMeeting(MeetingRequestDTO dto, Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("Organisateur non trouvé"));

        if (organizer.getRole() == null || organizer.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Seuls les médecins peuvent organiser une réunion");
        }

        Meeting.MeetingBuilder builder = Meeting.builder()
                .title(dto.getTitle())
                .dateTime(dto.getDateTime())
                .meetingLink(dto.getMeetingLink())
                .organizer(organizer)
                .recorded(Boolean.TRUE.equals(dto.getRecorded()))
                .status(MeetingStatus.PLANNED);

        // Points d'ordre du jour
        if (dto.getAgendaPoints() != null && !dto.getAgendaPoints().isEmpty()) {
            builder.agendaPoints(String.join("\n", dto.getAgendaPoints()));
        }

        Meeting meeting = builder.build();

        // Ajout des participants si fournis
        if (dto.getParticipantIds() != null && !dto.getParticipantIds().isEmpty()) {
            Set<User> participants = new HashSet<>(userRepository.findAllById(dto.getParticipantIds()));
            meeting.setParticipants(participants);
        }

        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    @Override
    public List<MeetingResponseDTO> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingResponseDTO getMeetingById(Long id) {
        return mapToResponseDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public MeetingResponseDTO updateMeeting(Long id, MeetingRequestDTO dto, Long organizerId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, organizerId);

        if (meeting.getStatus() == MeetingStatus.FINISHED || meeting.getStatus() == MeetingStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier une réunion terminée ou annulée");
        }

        meeting.setTitle(dto.getTitle());
        meeting.setDateTime(dto.getDateTime());
        meeting.setMeetingLink(dto.getMeetingLink());
        meeting.setRecorded(Boolean.TRUE.equals(dto.getRecorded()));

        if (dto.getAgendaPoints() != null) {
            meeting.setAgendaPoints(String.join("\n", dto.getAgendaPoints()));
        }

        if (dto.getParticipantIds() != null) {
            Set<User> participants = new HashSet<>(userRepository.findAllById(dto.getParticipantIds()));
            meeting.setParticipants(participants);
        }

        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    @Override
    @Transactional
    public void deleteMeeting(Long id, Long organizerId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, organizerId);
        meetingRepository.delete(meeting);
    }

    // ── Gestion des participants ──────────────────────────────────────────────

    @Override
    @Transactional
    public MeetingResponseDTO addParticipant(Long meetingId, Long userId, Long requesterId) {
        Meeting meeting = findOrThrow(meetingId);
        assertIsOrganizer(meeting, requesterId);

        User participant = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + userId + " non trouvé"));

        meeting.getParticipants().add(participant);
        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    @Override
    @Transactional
    public MeetingResponseDTO removeParticipant(Long meetingId, Long userId, Long requesterId) {
        Meeting meeting = findOrThrow(meetingId);
        assertIsOrganizer(meeting, requesterId);

        meeting.getParticipants().removeIf(p -> p.getId().equals(userId));
        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    // ── Cycle de vie ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MeetingResponseDTO startMeeting(Long id, StartMeetingRequest request, Long requesterId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, requesterId);

        if (meeting.getStatus() != MeetingStatus.PLANNED) {
            throw new IllegalStateException(
                    "La réunion ne peut être démarrée que depuis l'état PLANNED (état actuel : " + meeting.getStatus() + ")");
        }

        meeting.setStatus(MeetingStatus.LIVE);
        meeting.setStartedAt(LocalDateTime.now());

        // Mise à jour du lien si fourni dans la requête
        if (request != null && request.getMeetingLink() != null && !request.getMeetingLink().isBlank()) {
            meeting.setMeetingLink(request.getMeetingLink());
        }

        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    @Override
    @Transactional
    public MeetingResponseDTO endMeeting(Long id, Long requesterId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, requesterId);

        if (meeting.getStatus() != MeetingStatus.LIVE) {
            throw new IllegalStateException(
                    "La réunion ne peut être terminée que depuis l'état LIVE (état actuel : " + meeting.getStatus() + ")");
        }

        meeting.setStatus(MeetingStatus.FINISHED);
        meeting.setEndedAt(LocalDateTime.now());

        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    @Override
    @Transactional
    public MeetingResponseDTO cancelMeeting(Long id, Long requesterId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, requesterId);

        if (meeting.getStatus() == MeetingStatus.FINISHED) {
            throw new IllegalStateException("Impossible d'annuler une réunion déjà terminée");
        }

        meeting.setStatus(MeetingStatus.CANCELLED);
        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    // ── Live ─────────────────────────────────────────────────────────────────

    @Override
    public MeetingLiveResponseDTO getLiveMeeting(Long id) {
        Meeting meeting = findOrThrow(id);

        Set<MeetingLiveResponseDTO.ParticipantInfo> participantInfos = meeting.getParticipants().stream()
                .map(p -> MeetingLiveResponseDTO.ParticipantInfo.builder()
                        .id(p.getId())
                        .fullName(p.getFullName())
                        .email(p.getEmail())
                        .build())
                .collect(Collectors.toSet());

        return MeetingLiveResponseDTO.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .status(meeting.getStatus())
                .meetingLink(meeting.getMeetingLink())
                .organizerId(meeting.getOrganizer().getId())
                .organizerName(meeting.getOrganizer().getFullName())
                .participants(participantInfos)
                .scheduledAt(meeting.getDateTime())
                .startedAt(meeting.getStartedAt())
                .agendaPoints(parseAgendaPoints(meeting.getAgendaPoints()))
                .meetingNotes(meeting.getMeetingNotes())
                .build();
    }

    @Override
    @Transactional
    public MeetingResponseDTO updateNotes(Long id, UpdateMeetingNotesRequest request, Long requesterId) {
        Meeting meeting = findOrThrow(id);

        // Les notes peuvent être mises à jour par l'organisateur ou un participant
        boolean isOrganizer = meeting.getOrganizer().getId().equals(requesterId);
        boolean isParticipant = meeting.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(requesterId));

        if (!isOrganizer && !isParticipant) {
            throw new IllegalArgumentException("Seuls l'organisateur et les participants peuvent modifier les notes");
        }

        if (meeting.getStatus() != MeetingStatus.LIVE) {
            throw new IllegalStateException("Les notes ne peuvent être modifiées que pendant une réunion en cours");
        }

        meeting.setMeetingNotes(request.getMeetingNotes());
        return mapToResponseDTO(meetingRepository.save(meeting));
    }

    // ── PV ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MeetingPvResponseDTO generatePv(Long id, String additionalInstructions, Long requesterId) {
        Meeting meeting = findOrThrow(id);
        assertIsOrganizer(meeting, requesterId);

        if (meeting.getStatus() != MeetingStatus.FINISHED) {
            throw new IllegalStateException(
                    "Le PV ne peut être généré que pour une réunion terminée (état actuel : " + meeting.getStatus() + ")");
        }

        String pvContent = aiPvService.generatePv(meeting, additionalInstructions);
        meeting.setPvContent(pvContent);
        meeting.setPvGenerated(true);
        meetingRepository.save(meeting);

        return mapToPvResponseDTO(meeting);
    }

    @Override
    public MeetingPvResponseDTO getPv(Long id) {
        Meeting meeting = findOrThrow(id);

        if (meeting.getStatus() != MeetingStatus.FINISHED) {
            throw new IllegalStateException(
                    "Le PV n'est disponible que pour une réunion terminée (état actuel : " + meeting.getStatus() + ")");
        }

        return mapToPvResponseDTO(meeting);
    }

    // ── STT ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SttResponse transcribeAudio(Long id, MultipartFile audioFile, String language) {
        Meeting meeting = findOrThrow(id);

        if (meeting.getStatus() != MeetingStatus.LIVE) {
            throw new IllegalStateException(
                    "La transcription n'est disponible que pendant une réunion en cours (état actuel : "
                    + meeting.getStatus() + ")");
        }

        // Appel Whisper (retourne "" si clé non configurée ou erreur)
        String segmentText = sttService.transcribe(audioFile, language);

        // Accumulation dans le champ transcription
        if (segmentText != null && !segmentText.isBlank()) {
            String existing = meeting.getTranscription();
            String updated = (existing == null || existing.isBlank())
                    ? segmentText
                    : existing + " " + segmentText;
            meeting.setTranscription(updated);
            meetingRepository.save(meeting);
        }

        return SttResponse.builder()
                .text(segmentText != null ? segmentText : "")
                .fullTranscription(meeting.getTranscription() != null ? meeting.getTranscription() : "")
                .build();
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    private Meeting findOrThrow(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réunion avec ID " + id + " non trouvée"));
    }

    private void assertIsOrganizer(Meeting meeting, Long userId) {
        if (!meeting.getOrganizer().getId().equals(userId)) {
            throw new IllegalArgumentException("Seul l'organisateur peut effectuer cette action");
        }
    }

    private List<String> parseAgendaPoints(String agendaPoints) {
        if (agendaPoints == null || agendaPoints.isBlank()) return List.of();
        return Arrays.stream(agendaPoints.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private MeetingResponseDTO mapToResponseDTO(Meeting meeting) {
        return MeetingResponseDTO.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .dateTime(meeting.getDateTime())
                .meetingLink(meeting.getMeetingLink())
                .organizerId(meeting.getOrganizer().getId())
                .organizerName(meeting.getOrganizer().getFullName())
                .recorded(meeting.isRecorded())
                .recordingUrl(meeting.getRecordingUrl())
                .status(meeting.getStatus())
                .startedAt(meeting.getStartedAt())
                .endedAt(meeting.getEndedAt())
                .agendaPoints(parseAgendaPoints(meeting.getAgendaPoints()))
                .meetingNotes(meeting.getMeetingNotes())
                .pvGenerated(meeting.isPvGenerated())
                .participantIds(meeting.getParticipants() != null
                        ? meeting.getParticipants().stream().map(User::getId).collect(Collectors.toSet())
                        : new HashSet<>())
                .participantNames(meeting.getParticipants() != null
                        ? meeting.getParticipants().stream().map(User::getFullName).collect(Collectors.toSet())
                        : new HashSet<>())
                .build();
    }

    private MeetingPvResponseDTO mapToPvResponseDTO(Meeting meeting) {
        return MeetingPvResponseDTO.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .status(meeting.getStatus())
                .organizerName(meeting.getOrganizer().getFullName())
                .participantNames(meeting.getParticipants() != null
                        ? meeting.getParticipants().stream().map(User::getFullName).collect(Collectors.toSet())
                        : new HashSet<>())
                .scheduledAt(meeting.getDateTime())
                .startedAt(meeting.getStartedAt())
                .endedAt(meeting.getEndedAt())
                .agendaPoints(parseAgendaPoints(meeting.getAgendaPoints()))
                .meetingNotes(meeting.getMeetingNotes())
                .pvContent(meeting.getPvContent())
                .pvGenerated(meeting.isPvGenerated())
                .build();
    }
}
