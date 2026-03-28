package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnotationRequestDTO {

    @NotBlank(message = "Le contenu de l'annotation est obligatoire")
    private String content;

    private Float positionX;

    private Float positionY;
}
