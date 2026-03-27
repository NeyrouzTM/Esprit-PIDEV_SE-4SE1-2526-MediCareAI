package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_item_prescription"))
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_prescription_item_medicine"))
    private Medicine medicine;

    private Integer quantity;          // total dispensed amount
    private String dosage;             // e.g., "500mg"
    private String frequency;          // e.g., "twice daily"
    private Integer durationDays;      // e.g., 7
    private String instructions;       // e.g., "take after meals"
    private Integer refills;           // e.g., 1

    // constructors, getters, setters
}
