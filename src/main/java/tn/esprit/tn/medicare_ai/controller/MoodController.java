package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.request.MoodRequest;
import tn.esprit.tn.medicare_ai.dto.response.MoodResponse;
import tn.esprit.tn.medicare_ai.service.IMoodService;
import java.util.List;

@RestController
@RequestMapping("/moods")
@RequiredArgsConstructor
@Tag(name = "Mood", description = "CRUD for mood entries")
public class MoodController {

    private final IMoodService moodService;

    @PostMapping
    @Operation(summary = "Create a mood entry")
    public ResponseEntity<MoodResponse> create(@Valid @RequestBody MoodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moodService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mood by ID")
    public ResponseEntity<MoodResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(moodService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all moods")
    public ResponseEntity<List<MoodResponse>> getAll() {
        return ResponseEntity.ok(moodService.getAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get moods by user ID")
    public ResponseEntity<List<MoodResponse>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(moodService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a mood entry")
    public ResponseEntity<MoodResponse> update(@PathVariable Long id, @Valid @RequestBody MoodRequest request) {
        return ResponseEntity.ok(moodService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mood entry")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
