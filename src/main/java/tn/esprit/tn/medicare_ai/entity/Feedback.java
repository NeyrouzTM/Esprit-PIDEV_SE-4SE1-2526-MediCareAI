package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

@Table(name = "feedbacks")

public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "User name is required")
    @Size(max = 100)
    private String userName;

    @Column(length = 1000)
    @Size(max = 1000)
    private String comment;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int rating;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "health_event_id")
    private HealthEvent healthEvent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
