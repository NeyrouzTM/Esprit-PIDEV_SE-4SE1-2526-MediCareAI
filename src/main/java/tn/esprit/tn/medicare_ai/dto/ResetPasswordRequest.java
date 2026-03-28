package tn.esprit.tn.medicare_ai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Reset code is required")
    @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits")
    private String code;

    @NotBlank(message = "New password is required")
    private String newPassword;
}

