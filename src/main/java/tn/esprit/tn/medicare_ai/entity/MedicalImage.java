package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    private String imageType; // X-ray, MRI, CT scan
    private String imageUrl;
    private LocalDate uploadDate;

    @Column(columnDefinition = "TEXT")
    private String description;
}