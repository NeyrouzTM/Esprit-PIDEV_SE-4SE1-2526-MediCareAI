package tn.esprit.tn.medicare_ai.dto;

/**
 * Ranked doctor match for keyword-based recommendation (feeds UI / future ML ranker).
 */
public record PhysicianRecommendationDto(
        Long doctorId,
        String fullName,
        String email,
        String clinicalDepartment,
        String clinicalKeywords,
        int matchScore,
        String matchedSignals
) {
}
