package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToMany
    @JoinTable(
        name = "user_health_events",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "health_event_id")
    )
    private List<HealthEvent> events = new java.util.ArrayList<>();

    public boolean isPremium() {
        return isPremium;
    }
}