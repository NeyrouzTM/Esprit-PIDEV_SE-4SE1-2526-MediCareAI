package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "replies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le contenu de la réponse est obligatoire")
    @Size(min = 5, max = 2000, message = "La réponse doit contenir entre 5 et 2000 caractères")
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * IDs des utilisateurs ayant liké cette réponse.
     */
    @ElementCollection
    @CollectionTable(name = "reply_likes", joinColumns = @JoinColumn(name = "reply_id"))
    @Column(name = "user_id")
    @Builder.Default
    private List<Long> likedByUserIds = new ArrayList<>();
}
