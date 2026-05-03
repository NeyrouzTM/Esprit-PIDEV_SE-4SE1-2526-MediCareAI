package tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyResponseDTO {

    private Long id;
    private String name;
    private String description;

    // optionnel
    private List<String> diseases;
}