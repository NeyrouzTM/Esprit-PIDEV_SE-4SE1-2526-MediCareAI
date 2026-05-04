package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.scheduling.*;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.entity.Availability;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentSchedulingService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_DURATION_MINUTES = 30;
    private static final int SLOT_STEP_MINUTES = 15;
    private static final int SEARCH_HORIZON_DAYS = 21;
    private static final int MIN_LEAD_STANDARD_MINUTES = 120;
    private static final int MIN_LEAD_URGENT_MINUTES = 20;
    private static final int TURNAROUND_BUFFER_MINUTES = 10;

    public List<AvailabilityConflictDto> detectConflicts(AvailabilityConflictCheckRequest request) {
        if (request == null || request.getAppointment() == null) {
            return List.of();
        }
        AppointmentWindowPayload p = request.getAppointment();
        LocalDateTime start = parseIso(p.getStartTime());
        LocalDateTime end = parseIso(p.getEndTime());
        Long excludeId = request.getExcludeAppointmentId();

        List<AvailabilityConflictDto> out = new ArrayList<>();

        if (start == null || end == null) {
            out.add(new AvailabilityConflictDto(
                    "INVALID_DURATION",
                    "BLOCKER",
                    "Start and end time are required (ISO-8601).",
                    null,
                    null
            ));
            return out;
        }
        if (!end.isAfter(start)) {
            out.add(new AvailabilityConflictDto(
                    "INVALID_DURATION",
                    "BLOCKER",
                    "Appointment end must be after the start.",
                    null,
                    null
            ));
            return out;
        }

        int durationMin = (int) Duration.between(start, end).toMinutes();
        if (durationMin < 15) {
            out.add(new AvailabilityConflictDto(
                    "INVALID_DURATION",
                    "BLOCKER",
                    "Minimum consultation length is 15 minutes.",
                    null,
                    null
            ));
            return out;
        }

        Long doctorId = p.getDoctorId();
        Long patientId = p.getPatientId();
        if (doctorId == null || patientId == null) {
            out.add(new AvailabilityConflictDto(
                    "INVALID_DURATION",
                    "BLOCKER",
                    "Doctor and patient are required for validation.",
                    null,
                    null
            ));
            return out;
        }

        boolean urgent = Boolean.TRUE.equals(p.getUrgent());
        int lead = urgent ? MIN_LEAD_URGENT_MINUTES : MIN_LEAD_STANDARD_MINUTES;
        LocalDateTime earliest = LocalDateTime.now().plusMinutes(lead);
        if (start.isBefore(earliest)) {
            out.add(new AvailabilityConflictDto(
                    "LEAD_TIME_VIOLATION",
                    "BLOCKER",
                    urgent
                            ? "Urgent bookings still require at least " + MIN_LEAD_URGENT_MINUTES + " minutes lead time."
                            : "Standard bookings require at least " + MIN_LEAD_STANDARD_MINUTES + " minutes lead time.",
                    null,
                    null
            ));
        }

        for (Appointment a : appointmentRepository.findByDoctorId(doctorId)) {
            if (!isActiveAppointment(a)) {
                continue;
            }
            if (excludeId != null && excludeId.equals(a.getId())) {
                continue;
            }
            if (intervalsOverlap(start, end, a.getAppointmentDate(), effectiveEnd(a))) {
                out.add(new AvailabilityConflictDto(
                        "DOCTOR_OVERLAP",
                        "BLOCKER",
                        "Doctor already has an overlapping appointment.",
                        a.getId(),
                        null
                ));
                break;
            }
        }

        for (Appointment a : appointmentRepository.findByPatientId(patientId)) {
            if (!isActiveAppointment(a)) {
                continue;
            }
            if (excludeId != null && excludeId.equals(a.getId())) {
                continue;
            }
            if (intervalsOverlap(start, end, a.getAppointmentDate(), effectiveEnd(a))) {
                out.add(new AvailabilityConflictDto(
                        "PATIENT_OVERLAP",
                        "BLOCKER",
                        "Patient already has an overlapping appointment.",
                        a.getId(),
                        null
                ));
                break;
            }
        }

        if (violatesTurnaround(doctorId, start, excludeId)) {
            out.add(new AvailabilityConflictDto(
                    "TURNAROUND_VIOLATION",
                    "WARNING",
                    "This slot is within " + TURNAROUND_BUFFER_MINUTES + " minutes after another visit for the same doctor.",
                    null,
                    null
            ));
        }

        if (!isWindowInsidePublishedAvailability(doctorId, start, end)) {
            out.add(new AvailabilityConflictDto(
                    "OUTSIDE_AVAILABILITY",
                    "WARNING",
                    "Proposed time is outside published availability blocks for this doctor.",
                    null,
                    null
            ));
        }

        return out;
    }

    @Transactional(readOnly = true)
    public List<AppointmentMatchCandidateDto> recommendMatches(AppointmentMatchRequestPayload request) {
        if (request == null) {
            return List.of();
        }
        LocalDateTime desiredStart = parseIso(request.getDesiredStartTime());
        LocalDateTime desiredEnd = parseIso(request.getDesiredEndTime());
        if (desiredStart == null || desiredEnd == null || !desiredEnd.isAfter(desiredStart)) {
            return List.of();
        }
        int durationMin = (int) Duration.between(desiredStart, desiredEnd).toMinutes();
        String consultationType = normalizeConsultationType(request.getConsultationType());

        List<AppointmentMatchCandidateDto> candidates = new ArrayList<>();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        List<Long> doctorOrder = new ArrayList<>();
        if (request.getPreferredDoctorId() != null) {
            doctorOrder.add(request.getPreferredDoctorId());
        }
        for (User u : userRepository.findByRole(Role.DOCTOR)) {
            if (!doctorOrder.contains(u.getId())) {
                doctorOrder.add(u.getId());
            }
        }

        for (Long doctorId : doctorOrder) {
            if (!userRepository.existsById(doctorId)) {
                continue;
            }
            for (LocalDate day = today; !day.isAfter(today.plusDays(SEARCH_HORIZON_DAYS)); day = day.plusDays(1)) {
                final LocalDate currentDay = day;
                List<Availability> daySlots = availabilityRepository.findByDoctor_IdAndAvailable(doctorId, true).stream()
                        .filter(av -> currentDay.equals(av.getDate()))
                        .sorted(Comparator.comparing(Availability::getStartTime))
                        .toList();

                for (Availability av : daySlots) {
                    LocalDateTime slotStartBoundary = LocalDateTime.of(av.getDate(), av.getStartTime());
                    LocalDateTime slotEndBoundary = LocalDateTime.of(av.getDate(), av.getEndTime());
                    LocalDateTime probe = slotStartBoundary;
                    while (!probe.plusMinutes(durationMin).isAfter(slotEndBoundary)) {
                        LocalDateTime candEnd = probe.plusMinutes(durationMin);
                        AvailabilityConflictCheckRequest probeReq = new AvailabilityConflictCheckRequest();
                        AppointmentWindowPayload p = new AppointmentWindowPayload();
                        p.setDoctorId(doctorId);
                        p.setPatientId(request.getPatientId());
                        p.setStartTime(probe.atZone(ZoneOffset.UTC).toInstant().toString());
                        p.setEndTime(candEnd.atZone(ZoneOffset.UTC).toInstant().toString());
                        p.setConsultationType(consultationType);
                        p.setUrgent(request.getUrgent());
                        p.setReasonForVisit(request.getReasonForVisit());
                        probeReq.setAppointment(p);
                        probeReq.setExcludeAppointmentId(null);

                        List<AvailabilityConflictDto> c = detectConflicts(probeReq);
                        boolean blocker = c.stream().anyMatch(x -> "BLOCKER".equals(x.severity()));
                        if (!blocker) {
                            int score = scoreCandidate(probe, desiredStart, doctorId, request.getPreferredDoctorId());
                            String confidence = score >= 85 ? "HIGH" : score >= 60 ? "MEDIUM" : "LOW";
                            List<String> reasons = new ArrayList<>();
                            reasons.add(Objects.equals(doctorId, request.getPreferredDoctorId()) ? "Preferred practitioner slot" : "Alternate practitioner slot");
                            reasons.add("Fits published availability and clears conflict checks.");

                            candidates.add(new AppointmentMatchCandidateDto(
                                    doctorId,
                                    probe.atZone(ZoneOffset.UTC).toInstant().toString(),
                                    candEnd.atZone(ZoneOffset.UTC).toInstant().toString(),
                                    consultationType,
                                    score,
                                    confidence,
                                    reasons,
                                    c
                            ));
                        }
                        probe = probe.plusMinutes(SLOT_STEP_MINUTES);
                    }
                }
            }
        }

        if (candidates.isEmpty() && !doctorOrder.isEmpty() && request.getPatientId() != null) {
            Long doc = request.getPreferredDoctorId() != null ? request.getPreferredDoctorId() : doctorOrder.get(0);
            if (userRepository.existsById(doc)) {
                AvailabilityConflictCheckRequest probeReq = new AvailabilityConflictCheckRequest();
                AppointmentWindowPayload p = new AppointmentWindowPayload();
                p.setDoctorId(doc);
                p.setPatientId(request.getPatientId());
                p.setStartTime(desiredStart.atZone(ZoneOffset.UTC).toInstant().toString());
                p.setEndTime(desiredEnd.atZone(ZoneOffset.UTC).toInstant().toString());
                p.setConsultationType(consultationType);
                p.setUrgent(request.getUrgent());
                p.setReasonForVisit(request.getReasonForVisit());
                probeReq.setAppointment(p);
                probeReq.setExcludeAppointmentId(null);
                List<AvailabilityConflictDto> c = detectConflicts(probeReq);
                boolean blocker = c.stream().anyMatch(x -> "BLOCKER".equals(x.severity()));
                if (!blocker) {
                    List<String> reasons = new ArrayList<>();
                    reasons.add("Fits requested window (no published availability rows required for fallback).");
                    int score = scoreCandidate(desiredStart, desiredStart, doc, request.getPreferredDoctorId());
                    String confidence = score >= 85 ? "HIGH" : "MEDIUM";
                    candidates.add(new AppointmentMatchCandidateDto(
                            doc,
                            desiredStart.atZone(ZoneOffset.UTC).toInstant().toString(),
                            desiredEnd.atZone(ZoneOffset.UTC).toInstant().toString(),
                            consultationType,
                            score,
                            confidence,
                            reasons,
                            c
                    ));
                }
            }
        }

        return candidates.stream()
                .sorted(Comparator.comparingInt(AppointmentMatchCandidateDto::score).reversed())
                .limit(15)
                .collect(Collectors.toList());
    }

    private static int scoreCandidate(
            LocalDateTime candidateStart,
            LocalDateTime desiredStart,
            long doctorId,
            Long preferredDoctorId
    ) {
        int minutesDelta = (int) Math.abs(Duration.between(candidateStart, desiredStart).toMinutes());
        int score = 100 - Math.min(80, minutesDelta / 5);
        if (preferredDoctorId != null && preferredDoctorId.equals(doctorId)) {
            score = Math.min(100, score + 12);
        }
        return Math.max(0, score);
    }

    private boolean violatesTurnaround(Long doctorId, LocalDateTime proposedStart, Long excludeId) {
        LocalDateTime maxEndBefore = null;
        for (Appointment a : appointmentRepository.findByDoctorId(doctorId)) {
            if (!isActiveAppointment(a)) {
                continue;
            }
            if (excludeId != null && excludeId.equals(a.getId())) {
                continue;
            }
            LocalDateTime end = effectiveEnd(a);
            if (!end.isAfter(proposedStart)) {
                if (maxEndBefore == null || end.isAfter(maxEndBefore)) {
                    maxEndBefore = end;
                }
            }
        }
        if (maxEndBefore == null) {
            return false;
        }
        long gapMin = Duration.between(maxEndBefore, proposedStart).toMinutes();
        return gapMin >= 0 && gapMin < TURNAROUND_BUFFER_MINUTES;
    }

    private boolean isWindowInsidePublishedAvailability(Long doctorId, LocalDateTime start, LocalDateTime end) {
        List<Availability> blocks = availabilityRepository.findByDoctor_IdAndAvailable(doctorId, true);
        if (blocks.isEmpty()) {
            return true;
        }
        for (Availability av : blocks) {
            LocalDateTime bStart = LocalDateTime.of(av.getDate(), av.getStartTime());
            LocalDateTime bEnd = LocalDateTime.of(av.getDate(), av.getEndTime());
            if (!start.isBefore(bStart) && !end.isAfter(bEnd)) {
                return true;
            }
        }
        return false;
    }

    private static boolean intervalsOverlap(
            LocalDateTime s1,
            LocalDateTime e1,
            LocalDateTime s2,
            LocalDateTime e2
    ) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    private LocalDateTime effectiveEnd(Appointment a) {
        if (a.getAppointmentEndDate() != null) {
            return a.getAppointmentEndDate();
        }
        return a.getAppointmentDate().plusMinutes(DEFAULT_DURATION_MINUTES);
    }

    private boolean isActiveAppointment(Appointment a) {
        String s = a.getStatus();
        if (s == null) {
            return true;
        }
        return switch (s.toUpperCase(Locale.ROOT)) {
            case "CANCELLED", "COMPLETED", "EXPIRED" -> false;
            default -> true;
        };
    }

    private static LocalDateTime parseIso(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String s = raw.trim();
        try {
            return Instant.parse(s).atZone(ZoneOffset.UTC).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return OffsetDateTime.parse(s).toLocalDateTime();
            } catch (DateTimeParseException e2) {
                return LocalDateTime.parse(s);
            }
        }
    }

    private static String normalizeConsultationType(String ct) {
        if (ct == null || ct.isBlank()) {
            return "IN_PERSON";
        }
        String u = ct.trim().toUpperCase(Locale.ROOT);
        if (u.contains("VIDEO")) {
            return "VIDEO";
        }
        if (u.contains("PHONE")) {
            return "PHONE";
        }
        return "IN_PERSON";
    }
}
