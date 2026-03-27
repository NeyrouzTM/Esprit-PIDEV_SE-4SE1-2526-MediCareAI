package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
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

@Table(name = "health_events")

public class HealthEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Event title is required")
    @Size(max = 200, message = "Event title cannot exceed 200 characters")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Column(length = 1000)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(nullable = false)
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @Column(length = 500)
    @Size(max = 500, message = "Location cannot exceed 500 characters")
    private String location;

    @ManyToMany(mappedBy = "events")
    private List<User> participants;

    @OneToMany(mappedBy = "healthEvent", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}
