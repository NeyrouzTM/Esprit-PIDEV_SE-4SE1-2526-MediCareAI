package tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data // 🔥 Génère automatiquement getters, setters, toString, hashCode, equals

public class HealthEventRequestDTO {

        @NotBlank
        private String title;

        private String category;

        @Size(max = 1000)
        private String description;

        @Future
        private LocalDateTime eventDate;

        private String location;

        // getters & setters
}
