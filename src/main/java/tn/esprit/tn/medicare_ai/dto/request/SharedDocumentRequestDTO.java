package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedDocumentRequestDTO {

    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String fileName;

    @NotBlank(message = "L'URL du fichier est obligatoire")
    private String fileUrl;

    private Long sessionId;   // Optionnel si on le passe en paramètre
}
