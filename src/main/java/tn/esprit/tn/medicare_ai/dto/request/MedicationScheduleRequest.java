package tn.esprit.tn.medicare_ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationScheduleRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(min = 2, max = 200, message = "Medicine name must be between 2 and 200 characters")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Size(max = 100, message = "Frequency must not exceed 100 characters")
    private String frequency;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Record date is required")
    private LocalDate recordDate;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
