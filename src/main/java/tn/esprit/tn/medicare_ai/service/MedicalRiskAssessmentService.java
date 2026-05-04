package tn.esprit.tn.medicare_ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.MedicalRiskAssessmentDto;

import java.time.Instant;
import java.util.*;

@Service
public class MedicalRiskAssessmentService {

    public MedicalRiskAssessmentDto calculate(JsonNode body) {
        long patientId = body.path("patientId").asLong();
        long recordId = body.path("medicalRecordId").asLong(0);

        int chronicN = body.path("chronicDiseases").isArray() ? body.get("chronicDiseases").size() : 0;
        int allergiesN = body.path("allergies").isArray() ? body.get("allergies").size() : 0;
        int histN = body.path("medicalHistories").isArray() ? body.get("medicalHistories").size() : 0;

        JsonNode rx = body.get("prescriptions");
        int activeRx = 0;
        if (rx != null && rx.isArray()) {
            for (JsonNode p : rx) {
                String st = p.path("status").asText("ACTIVE");
                if (!"INACTIVE".equalsIgnoreCase(st)) {
                    activeRx++;
                }
            }
        }

        int labN = body.path("labResults").isArray() ? body.get("labResults").size() : 0;

        int chronicScore = Math.min(32, chronicN * 7);
        int allergyScore = Math.min(28, allergiesN * 6);
        int rxScore = Math.min(18, activeRx * 3);
        int histScore = Math.min(12, histN * 2);
        int labScore = Math.min(28, labN * 4);

        int overall = Math.min(100, chronicScore + allergyScore + rxScore + histScore + labScore + 4);

        String level = levelFor(overall);
        String chronicHay = extractChronicHaystack(body);
        Map<String, Integer> cats = Map.of(
                "cardiacRisk", cat(chronicHay, List.of("cardio", "heart", "hypertension", "coronary"), overall),
                "diabetesRisk", cat(chronicHay, List.of("diabetes", "glucose", "hba1c"), overall),
                "strokeRisk", cat(chronicHay, List.of("stroke", "avc", "tia"), overall),
                "cancerRisk", cat(chronicHay, List.of("cancer", "tumor", "oncolog"), overall),
                "renalRisk", cat(chronicHay, List.of("renal", "kidney", "dialysis", "creatinine"), overall),
                "respiratoryRisk", cat(chronicHay, List.of("asthma", "copd", "pulmonary", "respiratory"), overall),
                "mentalHealthRisk", cat(chronicHay, List.of("depression", "anxiety", "bipolar", "psychiat"), overall),
                "infectionRisk", Math.min(100, Math.round(allergyScore * 2.0f + labN * 2))
        );

        List<String> chronicList = new ArrayList<>();
        if (body.get("chronicDiseases") != null && body.get("chronicDiseases").isArray()) {
            for (JsonNode c : body.get("chronicDiseases")) {
                String n = c.path("name").asText(null);
                if (n != null && !n.isBlank()) {
                    chronicList.add(n);
                }
            }
        }

        List<String> allergyLabels = new ArrayList<>();
        if (body.get("allergies") != null && body.get("allergies").isArray()) {
            for (JsonNode a : body.get("allergies")) {
                String ag = a.path("allergen").asText("");
                String sev = a.path("severity").asText("");
                allergyLabels.add(sev.isBlank() ? ag : ag + " (" + sev + ")");
            }
        }

        return new MedicalRiskAssessmentDto(
                System.currentTimeMillis(),
                patientId,
                recordId,
                Instant.now().toString(),
                overall,
                level,
                cats,
                chronicList,
                allergyLabels,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                true,
                false,
                null
        );
    }

    private String extractChronicHaystack(JsonNode body) {
        StringBuilder sb = new StringBuilder();
        if (body.has("chronicDiseases") && body.get("chronicDiseases").isArray()) {
            for (JsonNode c : body.get("chronicDiseases")) {
                sb.append(c.path("name").asText("")).append(' ');
            }
        }
        return sb.toString().toLowerCase(Locale.ROOT);
    }

    private int cat(String haystack, List<String> keys, int overall) {
        boolean hit = keys.stream().anyMatch(haystack::contains);
        if (hit) {
            return Math.min(95, Math.max(35, overall + 12));
        }
        return (int) Math.min(40L, Math.round(overall * 0.35));
    }

    private String levelFor(int score) {
        if (score >= 80) {
            return "CRITICAL";
        }
        if (score >= 60) {
            return "HIGH";
        }
        if (score >= 40) {
            return "MODERATE";
        }
        return "LOW";
    }

    public List<String> recommendations(MedicalRiskAssessmentDto assessment) {
        List<String> rec = new ArrayList<>();
        int score = assessment.overallRiskScore() != null ? assessment.overallRiskScore() : 0;
        if (score > 70) {
            rec.add("Schedule a prompt medical visit.");
            rec.add("Step up monitoring of vitals and labs.");
        }
        var cats = assessment.riskCategories();
        if (cats != null && cats.getOrDefault("cardiacRisk", 0) > 50) {
            rec.add("Consider cardiology input and structured blood pressure follow-up.");
        }
        if (cats != null && cats.getOrDefault("diabetesRisk", 0) > 40) {
            rec.add("Glycemic follow-up (HbA1c, fasting glucose) if clinically indicated.");
        }
        if (assessment.medicationInteractionRisks() != null && !assessment.medicationInteractionRisks().isEmpty()) {
            rec.add("Medication review with the prescriber (drug interactions detected).");
        }
        if (assessment.laboratoryAnomalies() != null && !assessment.laboratoryAnomalies().isEmpty()) {
            rec.add("Correlate lab abnormalities with clinical context and repeat testing if needed.");
        }
        if (rec.isEmpty()) {
            rec.add("Continue routine follow-up.");
            rec.add("Adhere to treatment and scheduled visits.");
        }
        return rec;
    }
}
