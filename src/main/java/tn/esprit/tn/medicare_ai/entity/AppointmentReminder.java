package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "appointment_reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    /** When to fire (UTC). */
    @Column(nullable = false)
    private Instant remindAt;

    @Column(nullable = false, length = 32)
    private String channel;

    @Column(length = 32)
    private String provider;

    @Column(length = 32)
    @Builder.Default
    private String status = "SCHEDULED";

    private Instant sentAt;

    @Column(length = 255)
    private String providerMessageId;

    @Column(columnDefinition = "TEXT")
    private String failureReason;
}
