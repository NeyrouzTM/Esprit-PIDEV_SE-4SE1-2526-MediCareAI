package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationSessionRequestDTO {

    @NotBlank(message = "Le titre de la session est obligatoire")
    private String title;

    // On peut ajouter d'autres champs plus tard (description, type de cas, etc.)
}
