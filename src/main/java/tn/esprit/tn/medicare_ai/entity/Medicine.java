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
public class Medicine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String genericName;
    private String manufacturer;
    private String description;

    @Enumerated(EnumType.STRING)
    private MedicinieCategory category; // e.g., ANALGESIC, ANTIBIOTIC, etc.

    private String dosageForm;          // tablet, capsule, syrup, etc.
    private String strength;            // 500mg, 10mg/ml, etc.

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private Double price;
    private Boolean prescriptionRequired;
}
