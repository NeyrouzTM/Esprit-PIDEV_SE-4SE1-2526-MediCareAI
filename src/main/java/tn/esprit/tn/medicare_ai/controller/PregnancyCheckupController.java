package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyCheckupRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyCheckupResponse;
import tn.esprit.tn.medicare_ai.service.IPregnancyCheckupService;
import java.util.List;

@RestController
@RequestMapping("/pregnancy-checkups")
@RequiredArgsConstructor
@Tag(name = "PregnancyCheckup", description = "CRUD for PregnancyCheckup")
public class PregnancyCheckupController {

    private final IPregnancyCheckupService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<PregnancyCheckupResponse> create(@Valid @RequestBody PregnancyCheckupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<PregnancyCheckupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<PregnancyCheckupResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<PregnancyCheckupResponse> update(@PathVariable Long id, @Valid @RequestBody PregnancyCheckupRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
