package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.LabResultDTO;
import tn.esprit.tn.medicare_ai.service.LabResultService;

@RestController
@CrossOrigin("*")
@RequestMapping("/lab-results")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LabResultDTO dto) {
        return ResponseEntity.ok(labResultService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(labResultService.getById(id));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<?> getByMedicalRecordId(
            @PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(
                labResultService.getByMedicalRecordId(medicalRecordId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LabResultDTO dto) {
        return ResponseEntity.ok(labResultService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        labResultService.delete(id);
        return ResponseEntity.ok("Lab result deleted");
    }
}