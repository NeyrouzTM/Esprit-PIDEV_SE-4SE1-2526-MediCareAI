package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "activity")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Activity type is required")
    @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String type;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    @Column(nullable = false)
    private Integer duration;

    @NotBlank(message = "Benefit is required")
    @Size(min = 3, max = 500, message = "Benefit must be between 3 and 500 characters")
    @Column(nullable = false, length = 500)
    private String benefit;

    // ✅ FIXED: Changed to nullable = true
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregnancy_tracking_id", nullable = true, foreignKey = @ForeignKey(name = "fk_activity_pregnancy_tracking"))
    private PregnancyTracking pregnancyTracking;

    // Lien inverse avec Alert
    @OneToOne(mappedBy = "activity")
    private Alert alert;
}
