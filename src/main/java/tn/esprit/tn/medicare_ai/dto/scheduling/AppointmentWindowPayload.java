package tn.esprit.tn.medicare_ai.dto.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Mirrors Angular booking payloads that use ISO {@code startTime}/{@code endTime}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentWindowPayload {

    private Long doctorId;
    private Long patientId;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("endTime")
    private String endTime;

    private String consultationType;
    private Boolean urgent;
    private String reasonForVisit;
}
