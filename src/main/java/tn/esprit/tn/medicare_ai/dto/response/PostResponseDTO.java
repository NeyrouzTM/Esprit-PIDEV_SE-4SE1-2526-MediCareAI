package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private Set<String> tags;
    private Boolean isPremiumOnly;
    private int likesCount;
    private boolean likedByCurrentUser;
    private int repliesCount;
    private int viewsCount;
}
