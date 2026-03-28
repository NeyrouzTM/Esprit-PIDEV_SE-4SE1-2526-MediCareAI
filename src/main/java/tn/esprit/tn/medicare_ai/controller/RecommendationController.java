package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.RecommendationRequest;
import tn.esprit.tn.medicare_ai.dto.response.RecommendationResponse;
import tn.esprit.tn.medicare_ai.service.IRecommendationService;
import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "CRUD for Recommendation")
public class RecommendationController {

    private final IRecommendationService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<RecommendationResponse> create(@Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<RecommendationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<RecommendationResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<RecommendationResponse> update(@PathVariable Long id, @Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
