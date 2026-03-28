package tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymptomResponseDTO {
    private Long id;
    private String name;
    private String description;
}
