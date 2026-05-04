package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyTrackingRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyTrackingResponse;
import tn.esprit.tn.medicare_ai.service.IPregnancyTrackingService;
import java.util.List;

@RestController
@RequestMapping("/pregnancy-trackings")
@RequiredArgsConstructor
@Tag(name = "PregnancyTracking", description = "CRUD for PregnancyTracking")
public class PregnancyTrackingController {

    private final IPregnancyTrackingService service;

    @PostMapping
    @Operation(summary = "Create - currentWeek and dueDate are auto-calculated")
    public ResponseEntity<PregnancyTrackingResponse> create(@Valid @RequestBody PregnancyTrackingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<PregnancyTrackingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<PregnancyTrackingResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get by User ID")
    public ResponseEntity<List<PregnancyTrackingResponse>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update - currentWeek and dueDate are auto-recalculated")
    public ResponseEntity<PregnancyTrackingResponse> update(@PathVariable Long id, @Valid @RequestBody PregnancyTrackingRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


