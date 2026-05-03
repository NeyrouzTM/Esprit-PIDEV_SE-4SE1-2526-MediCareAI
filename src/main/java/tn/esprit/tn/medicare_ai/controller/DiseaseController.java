package tn.esprit.tn.medicare_ai.controller;

import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.DiseaseInterface;

import java.util.List;

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiseaseController {

    private final DiseaseInterface diseaseService;

    @PostMapping
    public ResponseEntity<DiseaseResponseDTO> create(@Valid @RequestBody DiseaseRequestDTO dto) {
        return ResponseEntity.ok(diseaseService.createDisease(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiseaseResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody DiseaseRequestDTO dto) {
        return ResponseEntity.ok(diseaseService.updateDisease(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diseaseService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiseaseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(diseaseService.getDisease(id));
    }

    @GetMapping
    public ResponseEntity<List<DiseaseResponseDTO>> getAll() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }
    // 🔹 Ajouter specialty
    @PostMapping("/{diseaseId}/specialty/{specialtyId}")
    public ResponseEntity<DiseaseResponseDTO> addSpecialty(
            @PathVariable Long diseaseId,
            @PathVariable Long specialtyId) {

        return ResponseEntity.ok(diseaseService.addSpecialty(diseaseId, specialtyId));
    }
    // 🔹 Ajouter symptom
    @PostMapping("/{diseaseId}/symptoms/{symptomId}")
    public ResponseEntity<DiseaseResponseDTO> addSymptom(
            @PathVariable Long diseaseId,
            @PathVariable Long symptomId) {

        return ResponseEntity.ok(diseaseService.addSymptom(diseaseId, symptomId));
    }
    // 🔹 Supprimer symptom
    @DeleteMapping("/{diseaseId}/symptoms/{symptomId}")
    public ResponseEntity<DiseaseResponseDTO> removeSymptom(
            @PathVariable Long diseaseId,
            @PathVariable Long symptomId) {

        return ResponseEntity.ok(diseaseService.removeSymptom(diseaseId, symptomId));
    }
}