package tn.esprit.tn.medicare_ai.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Le nom du plan est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom du plan doit contenir entre 3 et 100 caractères")
    private String name; // "Premium", "Basic", etc.

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer durationDays; // ex. 30 pour 1 mois

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;
}

