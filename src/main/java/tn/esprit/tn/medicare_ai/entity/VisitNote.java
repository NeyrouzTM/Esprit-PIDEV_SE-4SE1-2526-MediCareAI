package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    private LocalDateTime visitDate;

    @Column(columnDefinition = "TEXT")
    private String subjective; // SOAP - S

    @Column(columnDefinition = "TEXT")
    private String objective; // SOAP - O

    @Column(columnDefinition = "TEXT")
    private String assessment; // SOAP - A

    @Column(columnDefinition = "TEXT")
    private String plan; // SOAP - P

    private boolean finalized;
}