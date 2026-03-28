package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AllergyDTO;
import tn.esprit.tn.medicare_ai.service.AllergyService;

@RestController
@CrossOrigin("*")
@RequestMapping("/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AllergyDTO dto) {
        return ResponseEntity.ok(allergyService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(allergyService.getById(id));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<?> getByMedicalRecordId(
            @PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(
                allergyService.getByMedicalRecordId(medicalRecordId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AllergyDTO dto) {
        return ResponseEntity.ok(allergyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        allergyService.delete(id);
        return ResponseEntity.ok("Allergy deleted");
    }
}