package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pregnancy_checkup")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyCheckup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Checkup date is required")
    @PastOrPresent(message = "Checkup date cannot be in the future")
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "Observation is required")
    @Size(min = 3, max = 1000, message = "Observation must be between 3 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String observation;

    @DecimalMin(value = "30.0", message = "Weight must be at least 30 kg")
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300 kg")
    @Column(name = "weight_kg")
    private Double weightKg;

    @Size(max = 500, message = "Symptoms must not exceed 500 characters")
    @Column(length = 500)
    private String symptoms;

    @Min(value = 0, message = "Fetal movements cannot be negative")
    @Max(value = 200, message = "Fetal movements value is unrealistic")
    @Column(name = "fetal_movements")
    private Integer fetalMovements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregnancy_tracking_id", nullable = false)
    private PregnancyTracking pregnancyTracking;
}
