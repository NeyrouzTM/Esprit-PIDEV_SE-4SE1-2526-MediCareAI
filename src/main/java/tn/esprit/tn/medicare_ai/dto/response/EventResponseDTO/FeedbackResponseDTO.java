package tn.esprit.tn.medicare_ai.dto.response.EventResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data // 🔥 Génère automatiquement getters, setters, toString, hashCode, equals

public class FeedbackResponseDTO {


        private Long id;
        private String userName;
        private String comment;
        private int rating;
        private LocalDateTime createdAt;

        private Long healthEventId;
        private String healthEventTitle;

        private Long userId;

        // getters & setters

}
