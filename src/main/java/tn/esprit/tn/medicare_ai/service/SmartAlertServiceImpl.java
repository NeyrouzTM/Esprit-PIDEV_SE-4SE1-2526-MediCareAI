package tn.esprit.tn.medicare_ai.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.response.ActivityResponse;
import tn.esprit.tn.medicare_ai.dto.response.AlertResponse;
import tn.esprit.tn.medicare_ai.dto.response.RecommendationResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartAlertServiceImpl implements ISmartAlertService {

    private final AlertRepository alertRepository;
    private final SleepRepository sleepRepository;
    private final MoodRepository moodRepository;
    private final StressRepository stressRepository;
    private final RecommendationRepository recommendationRepository;
    private final ActivityRepository activityRepository;
    private final PregnancyTrackingRepository pregnancyTrackingRepository;
    private final UserRepository userRepository;

    /**
     * ==================== MAIN ANALYSIS METHOD ====================
     * Analyze health data for the last 30 days and create smart alerts
     */
    @Override
    @Transactional
    public void analyzeHealthDataAndCreateAlerts(Long userId) {
        log.info("🔍 Analyzing health data for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // Get data from last 30 days
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        // Fetch health data
        List<Sleep> sleepData = sleepRepository.findByUserId(userId);
        List<Mood> moodData = moodRepository.findByUserId(userId);
        List<Stress> stressData = stressRepository.findByUserId(userId);
        List<PregnancyTracking> pregnancyData = pregnancyTrackingRepository.findByUserId(userId);

        // === SHORT-TERM DETECTION (3-7 days) ===
        detectShortTermAlerts(userId, user, sleepData, moodData, sevenDaysAgo, threeDaysAgo);

        // === LONG-TERM DETECTION (7-30 days) ===
        detectLongTermAlerts(userId, user, sleepData, moodData, stressData, thirtyDaysAgo, sevenDaysAgo);

        // === PREGNANCY DETECTION ===
        if (!pregnancyData.isEmpty()) {
            detectPregnancyAlerts(userId, user, pregnancyData.get(0));
        }

        log.info("✅ Analysis completed for user: {}", userId);
    }

    /**
     * ==================== SHORT-TERM ALERTS (3-7 days) ====================
     * Detection: avgSleep < 6 AND mood low
     * Result: WARNING alert + Sleep improvement recommendation + 3-day activity
     */
    private void detectShortTermAlerts(Long userId, User user, List<Sleep> sleepData,
                                       List<Mood> moodData, LocalDate sevenDaysAgo, LocalDate threeDaysAgo) {

        // Filter data for last 7 days
        List<Sleep> recentSleep = sleepData.stream()
                .filter(s -> s.getDate().isAfter(sevenDaysAgo) || s.getDate().isEqual(sevenDaysAgo))
                .collect(Collectors.toList());

        List<Mood> recentMood = moodData.stream()
                .filter(m -> m.getDate().isAfter(sevenDaysAgo) || m.getDate().isEqual(sevenDaysAgo))
                .collect(Collectors.toList());

        // Calculate averages
        double avgSleep = recentSleep.isEmpty() ? 0 :
                recentSleep.stream().mapToDouble(Sleep::getHours).average().orElse(0);

        double avgMood = recentMood.isEmpty() ? 0 :
                recentMood.stream().mapToInt(Mood::getLevel).average().orElse(0);

        log.debug("📊 User {} - Avg Sleep: {}, Avg Mood: {}", userId, avgSleep, avgMood);

        // Condition: avgSleep < 6 AND avgMood < 5
        if (avgSleep < 6 && avgMood < 5) {
            log.warn("⚠️ SHORT-TERM ALERT TRIGGERED: Low sleep + low mood for user {}", userId);

            // Create Recommendation
            Recommendation recommendation = createSleepImprovement();

            // Create Activity
            Activity activity = createSleep7HoursActivity();

            // Create Alert
            Alert alert = Alert.builder()
                    .type(AlertType.SHORT_TERM)
                    .level(AlertLevel.WARNING)
                    .message("Low sleep detected (avg: " + String.format("%.1f", avgSleep) + "h) " +
                            "combined with low mood. Prioritize rest for next 3 days.")
                    .userId(userId)
                    .ignored(false)
                    .recommendation(recommendation)
                    .activity(activity)
                    .build();

            alertRepository.save(alert);
            log.info("✅ SHORT-TERM Alert created for user: {}", userId);
        }
    }

    /**
     * ==================== LONG-TERM ALERTS (7-30 days) ====================
     * Detection: Persistent stress/mood issues over 30 days
     * Result: WARNING alert + Rest/specialist recommendation + Workload reduction activity
     */
    private void detectLongTermAlerts(Long userId, User user, List<Sleep> sleepData,
                                      List<Mood> moodData, List<Stress> stressData,
                                      LocalDate thirtyDaysAgo, LocalDate sevenDaysAgo) {

        // Filter data for last 30 days
        List<Stress> monthStress = stressData.stream()
                .filter(s -> s.getDate().isAfter(thirtyDaysAgo) || s.getDate().isEqual(thirtyDaysAgo))
                .collect(Collectors.toList());

        List<Mood> monthMood = moodData.stream()
                .filter(m -> m.getDate().isAfter(thirtyDaysAgo) || m.getDate().isEqual(thirtyDaysAgo))
                .collect(Collectors.toList());

        if (monthStress.isEmpty() && monthMood.isEmpty()) return;

        // Calculate averages
        double avgStress = monthStress.isEmpty() ? 0 :
                monthStress.stream().mapToInt(Stress::getLevel).average().orElse(0);

        double avgMood30 = monthMood.isEmpty() ? 0 :
                monthMood.stream().mapToInt(Mood::getLevel).average().orElse(0);

        log.debug("📊 User {} - Avg Stress (30d): {}, Avg Mood (30d): {}", userId, avgStress, avgMood30);

        // Condition: High stress (>7) AND low mood (<5) persisting for 30 days
        if (avgStress > 7 && avgMood30 < 5) {
            log.warn("🔴 LONG-TERM ALERT TRIGGERED: Burnout risk for user {}", userId);

            // Check if alert already exists for this user
            boolean exists = alertRepository.findByUserIdAndType(userId, AlertType.LONG_TERM)
                    .stream()
                    .anyMatch(a -> !a.getIgnored());

            if (exists) return; // Avoid duplicate alerts

            // Create Recommendation
            Recommendation recommendation = createBurnoutRecommendation();

            // Create Activity
            Activity activity = createWorkloadReductionActivity();

            // Create Alert
            Alert alert = Alert.builder()
                    .type(AlertType.LONG_TERM)
                    .level(AlertLevel.WARNING)
                    .message("Burnout risk detected: High stress (avg: " + String.format("%.1f", avgStress) + "/10) " +
                            "and low mood persisting for 30 days. Consider consulting a specialist.")
                    .userId(userId)
                    .ignored(false)
                    .recommendation(recommendation)
                    .activity(activity)
                    .build();

            alertRepository.save(alert);
            log.info("✅ LONG-TERM Alert created for user: {}", userId);
        }
    }

    /**
     * ==================== PREGNANCY ALERTS ====================
     * Detection: Abnormal weight or low fetal movement
     * Result: URGENT alert + Doctor consultation recommendation + Medical visit activity
     */
    private void detectPregnancyAlerts(Long userId, User user, PregnancyTracking pregnancy) {
        log.info("🤰 Checking pregnancy alerts for user: {}", userId);

        // Simulated detection (in production: check weight, fetal movement, etc.)
        // For demo: we check if current week is critical (>36 weeks with issues)
        boolean hasAbnormalCondition = pregnancy.getCurrentWeek() > 36 &&
                shouldTriggerPregnancyAlert();

        if (hasAbnormalCondition) {
            log.warn("🚨 PREGNANCY ALERT TRIGGERED: Abnormal condition for user {}", userId);

            // Create Recommendation
            Recommendation recommendation = createDoctorConsultationRecommendation(pregnancy);

            // Create Activity
            Activity activity = createMedicalVisitActivity(pregnancy);

            // Create URGENT Alert
            Alert alert = Alert.builder()
                    .type(AlertType.PREGNANCY)
                    .level(AlertLevel.URGENT)
                    .message("Abnormal pregnancy condition detected at week " + pregnancy.getCurrentWeek() +
                            ". Consult your doctor immediately.")
                    .userId(userId)
                    .ignored(false)
                    .recommendation(recommendation)
                    .activity(activity)
                    .build();

            alertRepository.save(alert);
            log.info("✅ PREGNANCY Alert (URGENT) created for user: {}", userId);
        }
    }

    /**
     * ==================== ESCALATION LOGIC ====================
     * If alert ignored > 2 days → escalate to URGENT
     */
    @Override
    @Transactional
    public void escalateIgnoredAlerts(Long userId) {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        List<Alert> ignoredAlerts = alertRepository.findIgnoredAlertsOlderThan(userId, twoDaysAgo);

        for (Alert alert : ignoredAlerts) {
            if (alert.getLevel() != AlertLevel.URGENT) {
                log.warn("🔴 ESCALATING alert {} from {} to URGENT", alert.getId(), alert.getLevel());
                alert.setLevel(AlertLevel.URGENT);
                alertRepository.save(alert);
            }
        }
    }

    /**
     * ==================== API METHODS ====================
     */

    @Override
    public List<AlertResponse> getAlertsByUserId(Long userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getActiveAlertsByUserId(Long userId) {
        return alertRepository.findByUserIdAndIgnoredFalse(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponse ignoreAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found: " + alertId));

        alert.setIgnored(true);
        alert.setIgnoredAt(LocalDateTime.now());
        return toResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponse getAlertById(Long alertId) {
        return toResponse(alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found: " + alertId)));
    }

    @Override
    @Transactional
    public void deleteAlert(Long alertId) {
        if (!alertRepository.existsById(alertId))
            throw new EntityNotFoundException("Alert not found: " + alertId);
        alertRepository.deleteById(alertId);
    }

    /**
     * ==================== HELPER METHODS ====================
     */

    private Recommendation createSleepImprovement() {
        return Recommendation.builder()
                .description("Establish a consistent sleep schedule and aim for 7-8 hours nightly")
                .goal("Improve sleep quality to better manage mood and stress")
                .category(RecommendationCategory.HEALTH)
                .build();
    }

    private Activity createSleep7HoursActivity() {
        return Activity.builder()
                .type("Sleep Routine")
                .duration(480)  // 8 hours in minutes
                .benefit("Achieve at least 7 hours of quality sleep daily for 3 consecutive days")
                .build();
    }

    private Recommendation createBurnoutRecommendation() {
        return Recommendation.builder()
                .description("Take planned rest days and consider consulting a mental health specialist")
                .goal("Reduce chronic stress and prevent burnout")
                .category(RecommendationCategory.HEALTH)
                .build();
    }

    private Activity createWorkloadReductionActivity() {
        return Activity.builder()
                .type("Workload Management")
                .duration(60)  // 1 hour daily
                .benefit("Implement daily relaxation routine: meditation, yoga, or nature walk")
                .build();
    }

    private Recommendation createDoctorConsultationRecommendation(PregnancyTracking pregnancy) {
        return Recommendation.builder()
                .description("Schedule an immediate consultation with your obstetrician")
                .goal("Ensure fetal health and maternal safety")
                .category(RecommendationCategory.HEALTH)
                .pregnancyTracking(pregnancy)
                .build();
    }

    private Activity createMedicalVisitActivity(PregnancyTracking pregnancy) {
        return Activity.builder()
                .type("Medical Consultation")
                .duration(60)
                .benefit("Complete medical checkup and fetal monitoring at week " + pregnancy.getCurrentWeek())
                .pregnancyTracking(pregnancy)
                .build();
    }

    private boolean shouldTriggerPregnancyAlert() {
        // Simulated logic - in production: check actual metrics
        return Math.random() < 0.1; // 10% chance for demo
    }

    private AlertResponse toResponse(Alert alert) {
        RecommendationResponse recResponse = null;
        if (alert.getRecommendation() != null) {
            recResponse = RecommendationResponse.builder()
                    .id(alert.getRecommendation().getId())
                    .description(alert.getRecommendation().getDescription())
                    .goal(alert.getRecommendation().getGoal())
                    .category(alert.getRecommendation().getCategory())
                    .pregnancyTrackingId(alert.getRecommendation().getPregnancyTracking() != null ?
                            alert.getRecommendation().getPregnancyTracking().getId() : null)
                    .build();
        }

        ActivityResponse actResponse = null;
        if (alert.getActivity() != null) {
            actResponse = ActivityResponse.builder()
                    .id(alert.getActivity().getId())
                    .type(alert.getActivity().getType())
                    .duration(alert.getActivity().getDuration())
                    .benefit(alert.getActivity().getBenefit())
                    .pregnancyTrackingId(alert.getActivity().getPregnancyTracking() != null ?
                            alert.getActivity().getPregnancyTracking().getId() : null)
                    .build();
        }

        return AlertResponse.builder()
                .id(alert.getId())
                .type(alert.getType())
                .message(alert.getMessage())
                .level(alert.getLevel())
                .ignored(alert.getIgnored())
                .ignoredAt(alert.getIgnoredAt())
                .userId(alert.getUserId())
                .createdAt(alert.getCreatedAt())
                .recommendation(recResponse)
                .activity(actResponse)
                .build();
    }
}
