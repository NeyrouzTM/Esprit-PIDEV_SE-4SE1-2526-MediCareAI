package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.ActivityRequest;
import tn.esprit.tn.medicare_ai.dto.response.ActivityResponse;
import tn.esprit.tn.medicare_ai.service.IActivityService;
import java.util.List;

@RestController
@RequestMapping("/activitys")
@RequiredArgsConstructor
@Tag(name = "Activity", description = "CRUD for Activity")
public class ActivityController {

        private final IActivityService service;

    @PostMapping
    @Operation(summary = "Create")
    public ResponseEntity<ActivityResponse> create(@Valid @RequestBody ActivityRequest request) {
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by ID")
    public ResponseEntity<ActivityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all")
    public ResponseEntity<List<ActivityResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update")
    public ResponseEntity<ActivityResponse> update(@PathVariable Long id, @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
