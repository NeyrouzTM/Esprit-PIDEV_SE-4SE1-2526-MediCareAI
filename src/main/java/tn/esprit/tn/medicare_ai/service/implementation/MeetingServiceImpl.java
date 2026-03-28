package tn.esprit.tn.medicare_ai.service.implementation;
import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Meeting;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MeetingRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MeetingResponseDTO createMeeting(MeetingRequestDTO dto, Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("Organisateur non trouvé"));

        if (organizer.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Seuls les professionnels peuvent organiser une réunion");
        }

        Meeting meeting = Meeting.builder()
                .title(dto.getTitle())
                .dateTime(dto.getDateTime())
                .meetingLink(dto.getMeetingLink())
                .organizer(organizer)
                .recorded(dto.isRecorded())
                .build();

        Meeting saved = meetingRepository.save(meeting);
        return mapToResponseDTO(saved);
    }



    @Override
    public List<MeetingResponseDTO> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingResponseDTO getMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réunion avec ID " + id + " non trouvée"));
        return mapToResponseDTO(meeting);
    }

    @Override
    @Transactional
    public MeetingResponseDTO updateMeeting(Long id, MeetingRequestDTO dto, Long organizerId) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée"));

        if (!meeting.getOrganizer().getId().equals(organizerId)) {
            throw new IllegalArgumentException("Seul l'organisateur peut modifier cette réunion");
        }

        meeting.setTitle(dto.getTitle());
        meeting.setDateTime(dto.getDateTime());
        meeting.setMeetingLink(dto.getMeetingLink());
        meeting.setRecorded(dto.isRecorded());

        Meeting updated = meetingRepository.save(meeting);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteMeeting(Long id, Long organizerId) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée"));

        if (!meeting.getOrganizer().getId().equals(organizerId)) {
            throw new IllegalArgumentException("Seul l'organisateur peut supprimer cette réunion");
        }

        meetingRepository.delete(meeting);

    }
    private MeetingResponseDTO mapToResponseDTO(Meeting meeting) {
        MeetingResponseDTO dto = new MeetingResponseDTO();
        dto.setId(meeting.getId());
        dto.setTitle(meeting.getTitle());
        dto.setDateTime(meeting.getDateTime());
        dto.setMeetingLink(meeting.getMeetingLink());
        dto.setOrganizerId(meeting.getOrganizer().getId());
        dto.setOrganizerName(meeting.getOrganizer().getFullName());
        dto.setRecorded(meeting.isRecorded());
        dto.setRecordingUrl(meeting.getRecordingUrl());

        // Protection contre null
        dto.setParticipantIds(meeting.getParticipants() != null
                ? meeting.getParticipants().stream().map(User::getId).collect(Collectors.toSet())
                : new HashSet<>());

        return dto;
    }}
