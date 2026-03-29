package tn.esprit.tn.medicare_ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shared_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private CollaborationSession session;

    // ✅ CORRECTION PRINCIPALE
    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Annotation> annotations = new ArrayList<>();

    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}