package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.MedicalRiskAssessmentDto;
import tn.esprit.tn.medicare_ai.service.MedicalRiskAssessmentService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/medical-risk-assessments")
@RequiredArgsConstructor
public class MedicalRiskAssessmentController {

    private final MedicalRiskAssessmentService medicalRiskAssessmentService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MedicalRiskAssessmentDto> listAll() {
        return List.of();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MedicalRiskAssessmentDto> getForPatient(@PathVariable Long patientId) {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/calculate")
    @PreAuthorize("isAuthenticated()")
    public MedicalRiskAssessmentDto calculate(@RequestBody JsonNode body) {
        return medicalRiskAssessmentService.calculate(body);
    }

    @PostMapping("/recommendations")
    @PreAuthorize("isAuthenticated()")
    public List<String> recommendations(@RequestBody MedicalRiskAssessmentDto assessment) {
        return medicalRiskAssessmentService.recommendations(assessment);
    }

    @GetMapping("/trend/{patientId}")
    @PreAuthorize("isAuthenticated()")
    public List<MedicalRiskAssessmentDto> trend(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "30") int days) {
        return List.of();
    }

    @GetMapping("/medication-interactions")
    @PreAuthorize("isAuthenticated()")
    public List<MedicalRiskAssessmentDto.MedicationInteractionDto> medicationInteractions(
            @RequestParam(required = false) String prescriptionIds) {
        return List.of();
    }

    @PostMapping("/lab-anomalies")
    @PreAuthorize("isAuthenticated()")
    public List<MedicalRiskAssessmentDto.LabAnomalyDto> labAnomalies(@RequestBody JsonNode body) {
        return List.of();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public MedicalRiskAssessmentDto save(@RequestBody MedicalRiskAssessmentDto dto) {
        return dto;
    }
}
