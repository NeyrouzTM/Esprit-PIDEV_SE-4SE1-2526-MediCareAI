package tn.esprit.tn.medicare_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SymptomRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SymptomResponseDTO;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SymptomInterface;

import java.util.List;

@RestController
@RequestMapping("/symptoms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SymptomController {

    private final SymptomInterface symptomService;

    @PostMapping
    public ResponseEntity<SymptomResponseDTO> create(@Valid @RequestBody SymptomRequestDTO dto) {
        return ResponseEntity.ok(symptomService.createSymptom(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SymptomResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody SymptomRequestDTO dto) {
        return ResponseEntity.ok(symptomService.updateSymptom(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        symptomService.deleteSymptom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SymptomResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(symptomService.getSymptom(id));
    }

    @GetMapping
    public ResponseEntity<List<SymptomResponseDTO>> getAll() {
        return ResponseEntity.ok(symptomService.getAllSymptoms());
    }
}