package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.MedicationReminderRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationReminderResponse;
import tn.esprit.tn.medicare_ai.service.IMedicationReminderService;
import java.util.List;

@RestController
@RequestMapping("/medication-reminders")
@RequiredArgsConstructor
@Tag(name = "MedicationReminder", description = "CRUD for MedicationReminder")
public class MedicationReminderController {

    private final IMedicationReminderService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<MedicationReminderResponse> create(@Valid @RequestBody MedicationReminderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<MedicationReminderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<MedicationReminderResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<MedicationReminderResponse> update(@PathVariable Long id, @Valid @RequestBody MedicationReminderRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
