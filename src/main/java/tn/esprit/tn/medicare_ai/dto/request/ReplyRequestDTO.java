package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyRequestDTO {

    @NotBlank(message = "Le contenu de la réponse est obligatoire")
    @Size(min = 5, message = "La réponse doit contenir au moins 5 caractères")
    private String content;
}