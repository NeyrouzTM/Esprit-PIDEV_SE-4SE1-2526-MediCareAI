package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.StressRequest;
import tn.esprit.tn.medicare_ai.dto.response.StressResponse;
import tn.esprit.tn.medicare_ai.service.IStressService;
import java.util.List;

@RestController
@RequestMapping("/stresss")
@RequiredArgsConstructor
@Tag(name = "Stress", description = "CRUD for Stress")
public class StressController {

    private final IStressService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<StressResponse> create(@Valid @RequestBody StressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<StressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<StressResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<StressResponse> update(@PathVariable Long id, @Valid @RequestBody StressRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
