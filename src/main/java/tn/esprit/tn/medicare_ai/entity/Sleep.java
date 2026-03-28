package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "sleep")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sleep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sleep hours are required")
    @DecimalMin(value = "0.5", message = "Sleep hours must be at least 0.5")
    @DecimalMax(value = "24.0", message = "Sleep hours cannot exceed 24")
    @Column(nullable = false)
    private Float hours;

    @NotNull(message = "Sleep quality is required")
    @Min(value = 1, message = "Quality must be at least 1")
    @Max(value = 10, message = "Quality must be at most 10")
    @Column(nullable = false)
    private Integer quality;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
