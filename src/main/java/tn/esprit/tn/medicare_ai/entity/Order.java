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
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_order_patient"))
    private User patient;          // role = PATIENT

    @ManyToOne
    @JoinColumn(name = "prescription_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_order_prescription"))
    private Prescription prescription;

    private LocalDate orderDate;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String shippingAddress;
    private String trackingNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
