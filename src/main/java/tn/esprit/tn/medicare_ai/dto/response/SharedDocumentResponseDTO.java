package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedDocumentResponseDTO {

    private Long id;
    private String fileName;
    private String fileUrl;
    private Long sessionId;
    private Long uploaderId;
    private String uploaderName;
    private int annotationCount;
    private LocalDateTime uploadedAt;
}
