package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(nullable = false)
    private String testName;

    private String result;
    private String unit;
    private String normalRange;
    private LocalDate testDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}