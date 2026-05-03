package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medication_schedule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Medicine name is required")
    @Size(min = 2, max = 200, message = "Medicine name must be between 2 and 200 characters")
    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Size(max = 100, message = "Frequency must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String frequency;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "Record date is required")
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cascade: deleting a schedule removes all its reminders
    @OneToMany(mappedBy = "medicationSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicationReminder> reminders = new ArrayList<>();
}
