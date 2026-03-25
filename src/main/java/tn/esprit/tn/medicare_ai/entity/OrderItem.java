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
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_order_item_medicine"))
    private Medicine medicine;

    private Integer quantity;
    private Double unitPrice;      // price at purchase time (snapshot)
    private Double subtotal;       // calculated = quantity * unitPrice

    // Optional: track which prescription item this order fulfills
    @ManyToOne
    @JoinColumn(name = "prescription_item_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_order_item_prescription_item"))
    private PrescriptionItem prescriptionItem;

    // constructors, getters, setters

    @PrePersist
    @PreUpdate
    private void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = quantity * unitPrice;
        }
    }
}
