package tn.esprit.tn.medicare_ai.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String status;
    private String reason;
    private String consultationType;
    private String notes;
}