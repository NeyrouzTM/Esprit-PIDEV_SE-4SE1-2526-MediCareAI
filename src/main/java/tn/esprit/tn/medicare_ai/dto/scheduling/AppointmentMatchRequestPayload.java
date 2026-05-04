package tn.esprit.tn.medicare_ai.dto.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentMatchRequestPayload {

    private Long patientId;
    private Long preferredDoctorId;
    private String specialtyKeyword;
    private String desiredStartTime;
    private String desiredEndTime;
    private String consultationType;
    private Boolean urgent;
    private String reasonForVisit;
}
