package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.tn.medicare_ai.dto.response.CorrelationDTO;
import tn.esprit.tn.medicare_ai.service.CorrelationService;

import java.util.List;

@RestController
@RequestMapping("/api/correlations")
@RequiredArgsConstructor
@Tag(name = "Correlation", description = "Personalized correlations between sleep, mood and stress")
public class CorrelationController {

    private final CorrelationService correlationService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get personalized correlations for a user")
    public ResponseEntity<List<CorrelationDTO>> getCorrelations(@PathVariable Long userId) {
        return ResponseEntity.ok(correlationService.getCorrelations(userId));
    }
}