package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Alert type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertType type;

    @NotBlank(message = "Message is required")
    @Size(min = 5, max = 1000, message = "Message must be between 5 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String message;

    @NotNull(message = "Alert level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertLevel level;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ignored = false;

    @Column(name = "ignored_at")
    private LocalDateTime ignoredAt;

    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // OneToOne avec Recommendation - une recommendation par alerte
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recommendation_id", foreignKey = @ForeignKey(name = "fk_alert_recommendation"))
    private Recommendation recommendation;

    // OneToOne avec Activity - une activity par alerte
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "activity_id", foreignKey = @ForeignKey(name = "fk_alert_activity"))
    private Activity activity;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
