package tn.esprit.tn.medicare_ai.dto.request.EventRequestDTO;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data // 🔥 Génère automatiquement getters, setters, toString, hashCode, equals

public class FeedbackRequestDTO {

    @NotBlank
    private String userName;

    @Size(max = 1000)
    private String comment;

    @Min(1)
    @Max(5)
    private int rating;

    private Long healthEventId;

    private Long userId;

    // getters & setters
}
