package tn.esprit.tn.medicare_ai.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanRequestDTO {

    @NotBlank(message = "Le nom du plan est obligatoire")
    private String name;

    @Positive(message = "Le prix doit être positif")
    private double price;

    @Positive(message = "La durée doit être positive")
    private int durationDays;

    private String description;
}