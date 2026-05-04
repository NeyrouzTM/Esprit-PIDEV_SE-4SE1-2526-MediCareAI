package tn.esprit.tn.medicare_ai.dto.scheduling;

import java.util.List;

public record AppointmentMatchCandidateDto(
        long doctorId,
        String startTime,
        String endTime,
        String consultationType,
        int score,
        String confidence,
        List<String> reasons,
        List<AvailabilityConflictDto> conflicts
) {
}
