package tn.esprit.tn.medicare_ai.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "specialties")

public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Specialty name is required")
    @Size(max = 30, message = "Specialty name cannot exceed 30 characters")
    String name;

    @Column(length = 200)
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    String description;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    List<Disease> diseases;
}
