package tn.esprit.tn.medicare_ai.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "meetings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la réunion est obligatoire")
    @Size(min = 5, max = 150)
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private String meetingLink; // Lien WebRTC ou externe

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToMany
    @JoinTable(
            name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> participants = new HashSet<>();

    private boolean recorded = false;

    private String recordingUrl; // Lien S3

    // ── Statut du cycle de vie ──────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MeetingStatus status = MeetingStatus.PLANNED;

    /** Horodatage du démarrage effectif de la réunion */
    private LocalDateTime startedAt;

    /** Horodatage de la fin effective de la réunion */
    private LocalDateTime endedAt;

    // ── Points d'ordre du jour (stockés en JSON-like text, séparés par \n) ──
    @Column(columnDefinition = "TEXT")
    private String agendaPoints;

    // ── Notes prises pendant la réunion ────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String meetingNotes;

    // ── PV généré par l'IA ─────────────────────────────────────────────────
    @Column(columnDefinition = "LONGTEXT")
    private String pvContent;

    /** Indique si le PV a déjà été généré par l'IA */
    @Builder.Default
    private boolean pvGenerated = false;

    /**
     * Transcription accumulée produite par le STT (Speech-to-Text).
     * Chaque segment envoyé par le frontend est concaténé ici.
     */
    @Column(columnDefinition = "LONGTEXT")
    private String transcription;
}

