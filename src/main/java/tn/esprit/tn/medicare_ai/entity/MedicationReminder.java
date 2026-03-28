package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "medication_reminder")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Reminder time is required")
    @Column(nullable = false)
    private LocalTime time;

    @NotBlank(message = "Message is required")
    @Size(min = 3, max = 500, message = "Message must be between 3 and 500 characters")
    @Column(nullable = false, length = 500)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_schedule_id", nullable = false)
    private MedicationSchedule medicationSchedule;
}
