package tn.esprit.tn.medicare_ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Mirrors Angular MedicalRiskAssessment (JSON field names).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MedicalRiskAssessmentDto(
        Long id,
        Long patientId,
        Long medicalRecordId,
        String assessmentDate,
        Integer overallRiskScore,
        String riskLevel,
        Map<String, Integer> riskCategories,
        List<String> chronicDiseaseComplications,
        List<String> allergyRisks,
        List<MedicationInteractionDto> medicationInteractionRisks,
        List<LabAnomalyDto> laboratoryAnomalies,
        List<String> recommendedActions,
        List<String> recommendedInterventions,
        Boolean assessedByAI,
        Boolean assessedByDoctor,
        String doctorNotes
) {
    public record MedicationInteractionDto(
            Long prescriptionId,
            List<Long> conflictingPrescriptionIds,
            String severity,
            String description
    ) {
    }

    public record LabAnomalyDto(
            String testName,
            String result,
            String abnormalityType,
            String clinicalSignificance
    ) {
    }
}
