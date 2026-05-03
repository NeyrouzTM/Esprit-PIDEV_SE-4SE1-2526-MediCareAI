package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AvailabilityDTO;
import tn.esprit.tn.medicare_ai.service.AvailabilityService;

@RestController
@CrossOrigin("*")
@RequestMapping("/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody AvailabilityDTO dto) {
        return ResponseEntity.ok(availabilityService.create(dto));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getByDoctorId(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(
                availabilityService.getByDoctorId(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(
                availabilityService.getAvailableSlots(doctorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AvailabilityDTO dto) {
        return ResponseEntity.ok(
                availabilityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        availabilityService.delete(id);
        return ResponseEntity.ok("Availability deleted");
    }
}