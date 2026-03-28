package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.WellBeingMetricRequest;
import tn.esprit.tn.medicare_ai.dto.response.WellBeingMetricResponse;
import tn.esprit.tn.medicare_ai.service.IWellBeingMetricService;
import java.util.List;

@RestController
@RequestMapping("/well-being-metrics")
@RequiredArgsConstructor
@Tag(name = "WellBeingMetric", description = "CRUD for WellBeingMetric")
public class WellBeingMetricController {

    private final IWellBeingMetricService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<WellBeingMetricResponse> create(@Valid @RequestBody WellBeingMetricRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<WellBeingMetricResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<WellBeingMetricResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<WellBeingMetricResponse> update(@PathVariable Long id, @Valid @RequestBody WellBeingMetricRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
