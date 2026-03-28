package tn.esprit.tn.medicare_ai.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "annotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder   // ← C'EST CETTE ANNOTATION QUI GÉNÈRE .builder()
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Float positionX;
    private Float positionY;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private SharedDocument document;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {

         // Retourne la date de création de l'annotation

        return createdAt;

}}
