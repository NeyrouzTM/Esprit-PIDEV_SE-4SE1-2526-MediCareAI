package tn.esprit.tn.medicare_ai.dto.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailabilityConflictCheckRequest {

    private AppointmentWindowPayload appointment;
    private Long excludeAppointmentId;
}
