package tn.esprit.tn.medicare_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationResponse {
    private String message;
    private boolean success;
}

