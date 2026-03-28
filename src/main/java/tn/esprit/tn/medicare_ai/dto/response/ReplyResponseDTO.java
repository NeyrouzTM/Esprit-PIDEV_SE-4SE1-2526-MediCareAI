package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponseDTO {

    private Long id;
    private String content;
    private Long postId;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
}