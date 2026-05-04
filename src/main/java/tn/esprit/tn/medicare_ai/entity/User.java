package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import lombok.*;

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
}