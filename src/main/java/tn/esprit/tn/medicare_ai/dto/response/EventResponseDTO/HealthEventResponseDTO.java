package tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data // 🔥 Génère automatiquement getters, setters, toString, hashCode, equals

public class HealthEventResponseDTO {

    private Long id;
    private String title;
    private String category;
    private String description;
    private LocalDateTime eventDate;
    private String location;

    // getters & setters

}
