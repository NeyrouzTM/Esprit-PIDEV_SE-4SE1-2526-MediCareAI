package tn.esprit.tn.medicare_ai.dto.scheduling;

public record AvailabilityConflictDto(
        String type,
        String severity,
        String message,
        Long conflictingAppointmentId,
        Long availabilityId
) {
}
