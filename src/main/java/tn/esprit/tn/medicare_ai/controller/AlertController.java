package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.response.AlertResponse;
import tn.esprit.tn.medicare_ai.service.ISmartAlertService;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "Smart Alerts", description = "AI-powered health alert system")
public class AlertController {

    private final ISmartAlertService smartAlertService;

    /**
     * Analyze health data and generate smart alerts for a user
     */
    @PostMapping("/analyze/{userId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Analyze health data and create alerts",
            description = "Analyzes last 30 days of health data (sleep, mood, stress) " +
                    "and generates smart alerts with recommendations and activities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analysis completed"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<String> analyzeHealthData(@PathVariable Long userId) {
        smartAlertService.analyzeHealthDataAndCreateAlerts(userId);
        return ResponseEntity.ok("✅ Health data analysis completed for user: " + userId);
    }

    /**
     * Get all alerts for a user with nested recommendations and activities
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get all alerts for a user",
            description = "Returns all alerts with nested recommendations and activities")
    @ApiResponse(responseCode = "200", description = "List of alerts retrieved")
    public ResponseEntity<List<AlertResponse>> getUserAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(smartAlertService.getAlertsByUserId(userId));
    }

    /**
     * Get only active (non-ignored) alerts
     */
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get active alerts for a user",
            description = "Returns only non-ignored alerts that require attention")
    @ApiResponse(responseCode = "200", description = "Active alerts retrieved")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(smartAlertService.getActiveAlertsByUserId(userId));
    }

    /**
     * Get specific alert by ID
     */
    @GetMapping("/{alertId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get alert by ID",
            description = "Retrieves a specific alert with full details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert retrieved"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long alertId) {
        return ResponseEntity.ok(smartAlertService.getAlertById(alertId));
    }

    /**
     * Mark alert as ignored and trigger escalation check
     */
    @PostMapping("/{alertId}/ignore")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Mark alert as ignored",
            description = "User acknowledges the alert (will escalate to URGENT if ignored >2 days)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert marked as ignored"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertResponse> ignoreAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(smartAlertService.ignoreAlert(alertId));
    }

    /**
     * Escalate ignored alerts (internal/scheduled task)
     */
    @PostMapping("/escalate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Escalate ignored alerts",
            description = "System task: escalates alerts ignored for >2 days to URGENT")
    @ApiResponse(responseCode = "200", description = "Escalation completed")
    public ResponseEntity<String> escalateIgnoredAlerts(@PathVariable Long userId) {
        smartAlertService.escalateIgnoredAlerts(userId);
        return ResponseEntity.ok("✅ Alert escalation completed for user: " + userId);
    }

    /**
     * Delete alert
     */
    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete alert",
            description = "Admin only: permanently removes an alert")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alert deleted"),
            @ApiResponse(responseCode = "404", description = "Alert not found"),
            @ApiResponse(responseCode = "403", description = "Admin only")
    })
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        smartAlertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}
