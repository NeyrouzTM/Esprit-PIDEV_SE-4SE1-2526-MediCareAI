package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.MedicalImageDTO;
import tn.esprit.tn.medicare_ai.service.MedicalImageService;

@RestController
@CrossOrigin("*")
@RequestMapping("/medical-images")
@RequiredArgsConstructor
public class MedicalImageController {

    private final MedicalImageService medicalImageService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MedicalImageDTO dto) {
        return ResponseEntity.ok(medicalImageService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalImageService.getById(id));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<?> getByMedicalRecordId(
            @PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(
                medicalImageService.getByMedicalRecordId(medicalRecordId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody MedicalImageDTO dto) {
        return ResponseEntity.ok(medicalImageService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        medicalImageService.delete(id);
        return ResponseEntity.ok("Medical image deleted");
    }
}