package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyCheckupRequest {

    @NotNull(message = "Checkup date is required")
    @PastOrPresent(message = "Checkup date cannot be in the future")
    private LocalDate date;

    @NotBlank(message = "Observation is required")
    @Size(min = 3, max = 1000, message = "Observation must be between 3 and 1000 characters")
    private String observation;

    @DecimalMin(value = "30.0", message = "Weight must be at least 30 kg")
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300 kg")
    private Double weightKg;

    @Size(max = 500, message = "Symptoms must not exceed 500 characters")
    private String symptoms;

    @Min(value = 0, message = "Fetal movements cannot be negative")
    @Max(value = 200, message = "Fetal movements value is unrealistic")
    private Integer fetalMovements;

    @NotNull(message = "Pregnancy tracking ID is required")
    @Positive(message = "Pregnancy tracking ID must be positive")
    private Long pregnancyTrackingId;
}
