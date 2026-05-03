package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    private Boolean recorded;

    /** IDs des participants à inviter à la création */
    private Set<Long> participantIds;

    /** Points d'ordre du jour (liste libre) */
    private List<String> agendaPoints;
}
