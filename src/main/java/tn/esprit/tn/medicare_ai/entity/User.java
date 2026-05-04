package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private boolean isPremium = false;

    @Builder.Default
    private boolean enabled = true;

    /** Optional link to admin specialty catalog (doctors). */
    private Long specialtyId;

    /** Free-text department or clinic name (doctors) — used for matching & future ML. */
    @Column(columnDefinition = "TEXT")
    private String clinicalDepartment;

    /** Comma- or space-separated tags, e.g. "cardiology, heart failure" (doctors). */
    @Column(columnDefinition = "TEXT")
    private String clinicalKeywords;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "user_health_events",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "health_event_id")
    )
    private List<HealthEvent> events = new ArrayList<>();

    public boolean isPremium() {
        return isPremium;
    }
}
