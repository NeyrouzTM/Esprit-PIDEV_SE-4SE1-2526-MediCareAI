package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_patient"))
    private User patient;          // role = PATIENT

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_doctor"))
    private User doctor;           // role = DOCTOR

    @Column(nullable = false)
    private String medicationName;
    private String dosage;
    private String duration;
    private String instructions;
    private LocalDate prescriptionDate;
    private boolean active;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> items;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
}
