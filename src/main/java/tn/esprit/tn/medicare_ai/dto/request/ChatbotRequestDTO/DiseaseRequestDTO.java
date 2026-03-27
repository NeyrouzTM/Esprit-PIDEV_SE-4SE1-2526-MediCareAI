package tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseRequestDTO {

    @NotBlank(message = "Disease name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 200)
    private String causes;

    @Size(max = 200)
    private String treatment;

    @NotNull(message = "Specialty is required")
    private Long specialtyId;

    private List<Long> symptomIds;
}