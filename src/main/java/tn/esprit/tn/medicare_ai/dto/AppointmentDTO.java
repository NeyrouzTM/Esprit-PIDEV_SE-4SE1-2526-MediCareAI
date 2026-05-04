package tn.esprit.tn.medicare_ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    /** Maps JSON {@code endTime} from the Angular client. */
    @JsonProperty("endTime")
    private LocalDateTime endTime;

    private String status;

    @JsonAlias({ "reasonForVisit" })
    private String reason;

    private String consultationType;
    private String notes;
}