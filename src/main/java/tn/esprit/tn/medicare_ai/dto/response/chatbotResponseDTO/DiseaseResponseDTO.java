package tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String causes;
    private String treatment;

    private String specialtyName;

    private List<String> symptoms;
}
