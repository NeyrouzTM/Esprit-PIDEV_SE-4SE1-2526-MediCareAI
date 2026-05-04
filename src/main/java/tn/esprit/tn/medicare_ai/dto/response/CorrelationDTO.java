package tn.esprit.tn.medicare_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrelationDTO {
    private LocalDate date;
    private Float sleepHours;
    private Integer moodLevel;
    private Integer stressLevel;
    private String insight;

    public CorrelationDTO(LocalDate date, Float sleepHours, Integer moodLevel, Integer stressLevel) {
        this.date = date;
        this.sleepHours = sleepHours;
        this.moodLevel = moodLevel;
        this.stressLevel = stressLevel;
    }
}