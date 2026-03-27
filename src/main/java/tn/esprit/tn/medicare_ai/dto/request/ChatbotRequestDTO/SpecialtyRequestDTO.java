package tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyRequestDTO {

    @NotBlank(message = "Specialty name is required")
    @Size(max = 30, message = "Name cannot exceed 30 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;


}