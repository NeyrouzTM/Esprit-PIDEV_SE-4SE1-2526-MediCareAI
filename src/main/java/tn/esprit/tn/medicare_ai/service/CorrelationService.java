package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.response.CorrelationDTO;
import tn.esprit.tn.medicare_ai.repository.SleepRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorrelationService {

    private static final float LOW_SLEEP_THRESHOLD = 6.0f;
    private static final int HIGH_STRESS_THRESHOLD = 7;
    private static final int LOW_MOOD_THRESHOLD = 4;

    private final SleepRepository sleepRepository;

    @Transactional(readOnly = true)
    public List<CorrelationDTO> getCorrelations(Long userId) {
        List<CorrelationDTO> correlations = sleepRepository.findCorrelationsByUserId(userId);
        correlations.forEach(correlation -> correlation.setInsight(buildInsight(correlation)));
        return correlations;
    }

    private String buildInsight(CorrelationDTO correlation) {
        boolean lowSleep = correlation.getSleepHours() != null && correlation.getSleepHours() < LOW_SLEEP_THRESHOLD;
        boolean highStress = correlation.getStressLevel() != null && correlation.getStressLevel() >= HIGH_STRESS_THRESHOLD;
        boolean lowMood = correlation.getMoodLevel() != null && correlation.getMoodLevel() <= LOW_MOOD_THRESHOLD;

        if (lowSleep && highStress && lowMood) {
            return "Low sleep, high stress, and low mood detected. Prioritize rest, relaxation, and longer recovery time.";
        }
        if (lowSleep && highStress) {
            return "Low sleep combined with high stress. Try to increase sleep duration and reduce stress sources.";
        }
        if (lowSleep && lowMood) {
            return "Low sleep combined with a low mood. Better rest could help stabilize your mood.";
        }
        if (highStress) {
            return "High stress observed. Add a break, guided breathing, or a relaxing activity.";
        }
        if (lowMood) {
            return "Low mood observed. Monitor recovery and emotional factors throughout the day.";
        }
        return "No strong negative trend detected for this date.";
    }
}