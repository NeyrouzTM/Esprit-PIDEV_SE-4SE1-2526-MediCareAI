package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "mood")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Mood level is required")
    @Min(value = 1, message = "Mood level must be at least 1")
    @Max(value = 10, message = "Mood level must be at most 10")
    @Column(nullable = false)
    private Integer level;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    @Column(length = 500)
    private String note;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
