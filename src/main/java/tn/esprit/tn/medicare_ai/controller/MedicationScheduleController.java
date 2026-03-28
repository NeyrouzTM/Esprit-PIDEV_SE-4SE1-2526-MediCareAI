package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.MedicationScheduleRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationScheduleResponse;
import tn.esprit.tn.medicare_ai.service.IMedicationScheduleService;
import java.util.List;

@RestController
@RequestMapping("/medication-schedules")
@RequiredArgsConstructor
@Tag(name = "MedicationSchedule", description = "CRUD for MedicationSchedule")
public class MedicationScheduleController {

    private final IMedicationScheduleService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<MedicationScheduleResponse> create(@Valid @RequestBody MedicationScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<MedicationScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<MedicationScheduleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<MedicationScheduleResponse> update(@PathVariable Long id, @Valid @RequestBody MedicationScheduleRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
