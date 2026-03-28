package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pregnancy_tracking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PregnancyTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Current week is required")
    @Min(value = 1, message = "Current week must be at least 1")
    @Max(value = 42, message = "Current week cannot exceed 42")
    @Column(name = "current_week", nullable = false)
    private Integer currentWeek;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(length = 1000)
    private String notes;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // One pregnancy profile per user — enforced in service layer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cascade: deleting tracking removes checkups, recommendations and activities
    @OneToMany(mappedBy = "pregnancyTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PregnancyCheckup> checkups = new ArrayList<>();

    @OneToMany(mappedBy = "pregnancyTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "pregnancyTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Activity> activities = new ArrayList<>();
}
