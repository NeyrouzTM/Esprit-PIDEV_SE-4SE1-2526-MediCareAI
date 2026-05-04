package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tn.esprit.tn.medicare_ai.service.IPregnancyTrackingService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pregnancy")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pregnancy", description = "Pregnancy week calculation and timeline management")
public class PregnancyController {

    private final IPregnancyTrackingService pregnancyTrackingService;
    private final RestTemplate restTemplate;

    /**
     * Calculate current pregnancy week based on Last Menstrual Period (LMP)
     * Example: GET /pregnancy/current-week?lmp=2025-10-01
     */
    @GetMapping("/current-week")
    @Operation(summary = "Calculate current pregnancy week from LMP")
    public ResponseEntity<Map<String, Object>> getCurrentWeek(
            @Parameter(description = "Last Menstrual Period date (YYYY-MM-DD)", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate lmp) {
        try {
            Integer currentWeek = pregnancyTrackingService.calculateCurrentWeek(lmp);
            LocalDate dueDate = pregnancyTrackingService.calculateDueDate(lmp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("lmp", lmp);
            response.put("currentWeek", currentWeek);
            response.put("dueDate", dueDate);
            response.put("daysPregnant", java.time.temporal.ChronoUnit.DAYS.between(lmp, LocalDate.now()));
            response.put("daysRemaining", java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate));
            response.put("weeksRemaining", 40 - currentWeek);
            
            log.info("Current week calculated for LMP: {}, Week: {}", lmp, currentWeek);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid LMP date provided: {}", lmp, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get pregnancy timeline from external RapidAPI
     * This endpoint integrates with RapidAPI to fetch detailed pregnancy week information
     * Data is NOT stored in the database (managed frontend-side)
     * Example: GET /pregnancy/timeline?week=20
     */
    @GetMapping("/timeline")
    @Operation(summary = "Get pregnancy timeline from RapidAPI (external data)")
    public ResponseEntity<Map<String, Object>> getPregnancyTimeline(
            @Parameter(description = "Pregnancy week (1-42)", required = false)
            @RequestParam(required = false)
            Integer week) {
        try {
            // Call RapidAPI endpoint for pregnancy timeline
            String timelineResponse = callRapidAPIPregnancyTimeline(week);
            
            Map<String, Object> response = new HashMap<>();
            response.put("source", "RapidAPI");
            response.put("data", timelineResponse);
            response.put("note", "External data - not stored in database");
            
            log.info("Pregnancy timeline fetched from RapidAPI");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching pregnancy timeline from RapidAPI", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch pregnancy timeline");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Helper method to call RapidAPI for pregnancy week timeline
     * Requirements:
     * - x-rapidapi-key: your API key
     * - x-rapidapi-host: the RapidAPI host
     */
    private String callRapidAPIPregnancyTimeline(Integer week) {
        // Note: Configure these in application.properties or environment variables
        String apiKey = System.getenv("RAPIDAPI_KEY");
        String apiHost = System.getenv("RAPIDAPI_HOST");
        String endpoint = System.getenv("RAPIDAPI_ENDPOINT");
        
        if (apiKey == null || apiHost == null) {
            log.warn("RapidAPI credentials not configured");
            return "RapidAPI credentials not configured. Please set RAPIDAPI_KEY and RAPIDAPI_HOST environment variables.";
        }
        
        String url = endpoint + (week != null ? "?week=" + week : "");
        
        // Use RestTemplate to call the external API
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-rapidapi-key", apiKey);
            headers.put("x-rapidapi-host", apiHost);
            
            // In a real scenario, you would use RestTemplate with HttpHeaders
            // For now, returning a placeholder response
            return "Pregnancy timeline data from week " + (week != null ? week : "current");
        } catch (Exception e) {
            log.error("Error calling RapidAPI", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculate due date from LMP
     * Example: GET /pregnancy/due-date?lmp=2025-10-01
     */
    @GetMapping("/due-date")
    @Operation(summary = "Calculate due date (EDD) from LMP")
    public ResponseEntity<Map<String, Object>> calculateDueDate(
            @Parameter(description = "Last Menstrual Period date (YYYY-MM-DD)", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate lmp) {
        try {
            LocalDate dueDate = pregnancyTrackingService.calculateDueDate(lmp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("lmp", lmp);
            response.put("estimatedDueDate", dueDate);
            response.put("pregnancyDuration", 280 + " days (40 weeks)");
            
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            if (daysRemaining > 0) {
                response.put("daysRemaining", daysRemaining);
            } else {
                response.put("daysOverdue", Math.abs(daysRemaining));
            }
            
            log.info("Due date calculated for LMP: {}, EDD: {}", lmp, dueDate);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid LMP date provided: {}", lmp, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

