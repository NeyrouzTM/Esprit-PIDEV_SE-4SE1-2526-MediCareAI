package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DrugInteraction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_a_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_drug_interaction_medicine_a"))
    private Medicine medicineA;

    @ManyToOne
    @JoinColumn(name = "medicine_b_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_drug_interaction_medicine_b"))
    private Medicine medicineB;

    private String severity;   // MILD, MODERATE, SEVERE
    private String description;
    private String recommendation;
}