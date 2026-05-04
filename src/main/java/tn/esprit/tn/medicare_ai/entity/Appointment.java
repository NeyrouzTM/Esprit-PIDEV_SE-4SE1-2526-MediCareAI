package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    private LocalDateTime appointmentDate;

    /** End of the visit window; when null, scheduling logic assumes a default duration from start. */
    private LocalDateTime appointmentEndDate;

    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String reason;
    private String consultationType; // VIDEO, IN_PERSON

    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Last automated reminder email sent (optional audit). */
    private LocalDateTime reminderLastSentAt;

    @Builder.Default
    private int reminderEmailCount = 0;
}