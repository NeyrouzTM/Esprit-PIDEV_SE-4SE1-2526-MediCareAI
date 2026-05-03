package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.service.MedicalRecordService;

@RestController
@CrossOrigin("*")
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(medicalRecordService.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medicalRecordService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatientId(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                medicalRecordService.getByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(
                medicalRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.ok("Medical record deleted");
    }
}