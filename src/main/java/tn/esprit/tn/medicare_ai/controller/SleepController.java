package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.SleepRequest;
import tn.esprit.tn.medicare_ai.dto.response.SleepResponse;
import tn.esprit.tn.medicare_ai.service.ISleepService;
import java.util.List;

@RestController
@RequestMapping("/sleeps")
@RequiredArgsConstructor
@Tag(name = "Sleep", description = "CRUD for Sleep")
public class SleepController {

    private final ISleepService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<SleepResponse> create(@Valid @RequestBody SleepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<SleepResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<SleepResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<SleepResponse> update(@PathVariable Long id, @Valid @RequestBody SleepRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
