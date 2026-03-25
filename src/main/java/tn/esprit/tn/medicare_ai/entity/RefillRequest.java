package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RefillRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_refill_request_prescription"))
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_refill_request_patient"))
    private User patient;

    private LocalDate requestDate;
    private Integer requestedQuantity;

    @Enumerated(EnumType.STRING)
    private RefillStatus status; // PENDING, APPROVED, REJECTED, FULFILLED
}
