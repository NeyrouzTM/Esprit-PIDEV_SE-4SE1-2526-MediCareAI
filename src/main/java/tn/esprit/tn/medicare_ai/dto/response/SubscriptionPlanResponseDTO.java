package tn.esprit.tn.medicare_ai.dto.response;



import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanResponseDTO {

    private Long id;
    private String name;
    private double price;
    private int durationDays;
    private String description;
}