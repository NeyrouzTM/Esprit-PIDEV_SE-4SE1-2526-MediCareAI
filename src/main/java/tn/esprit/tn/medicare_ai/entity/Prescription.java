package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_patient"))
    private User patient;          // role = PATIENT

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_doctor"))
    private User doctor;           // role = DOCTOR

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> items;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
}
