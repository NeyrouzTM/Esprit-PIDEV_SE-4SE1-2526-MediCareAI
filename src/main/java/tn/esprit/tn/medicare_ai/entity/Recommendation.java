package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "recommendation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotBlank(message = "Goal is required")
    @Size(min = 3, max = 500, message = "Goal must be between 3 and 500 characters")
    @Column(nullable = false, length = 500)
    private String goal;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecommendationCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregnancy_tracking_id", nullable = false)
    private PregnancyTracking pregnancyTracking;
}
