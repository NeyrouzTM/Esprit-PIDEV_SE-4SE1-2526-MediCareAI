package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRequestDTO {

    @NotBlank(message = "Le titre de la réunion est obligatoire")
    private String title;

    @Future(message = "La date de la réunion doit être dans le futur")
    private LocalDateTime dateTime;

    private String meetingLink;

    private boolean recorded;
}
