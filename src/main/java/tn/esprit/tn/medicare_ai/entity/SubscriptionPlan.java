package tn.esprit.tn.medicare_ai.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Premium", "Basic", etc.

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer durationDays; // ex. 30 pour 1 mois

    private String description;
}

