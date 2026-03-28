package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnotationResponseDTO {

    private Long id;
    private String content;
    private Float positionX;
    private Float positionY;
    private Long documentId;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
}