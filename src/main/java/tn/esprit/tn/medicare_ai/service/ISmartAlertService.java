package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.response.AlertResponse;
import java.util.List;

public interface ISmartAlertService {

    /**
     * Analyze health data and create smart alerts
     */
    void analyzeHealthDataAndCreateAlerts(Long userId);

    /**
     * Get all alerts for a user (with nested recommendations and activities)
     */
    List<AlertResponse> getAlertsByUserId(Long userId);

    /**
     * Get active (non-ignored) alerts for a user
     */
    List<AlertResponse> getActiveAlertsByUserId(Long userId);

    /**
     * Mark alert as ignored and escalate if needed
     */
    AlertResponse ignoreAlert(Long alertId);

    /**
     * Get alert by ID with nested data
     */
    AlertResponse getAlertById(Long alertId);

    /**
     * Delete alert
     */
    void deleteAlert(Long alertId);

    /**
     * Escalate ignored alerts (change level to URGENT if ignored > 2 days)
     */
    void escalateIgnoredAlerts(Long userId);
}
