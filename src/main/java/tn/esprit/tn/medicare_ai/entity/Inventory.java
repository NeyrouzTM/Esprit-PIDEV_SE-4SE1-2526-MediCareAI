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
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_inventory_medicine"))
    private Medicine medicine;

    private Integer stockQuantity;
    private String warehouseLocation;

    @Version
    private Integer version;
}
