package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to create a new user")
public class CreateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be at most 120 characters")
    @Schema(description = "User full name", example = "Ahmed Ben Ali")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 180, message = "Email must be at most 180 characters")
    @Schema(description = "Unique user email address", example = "ahmed@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 128, message = "Password must be between 6 and 128 characters")
    @Schema(description = "Raw password (will be hashed before saving)", example = "StrongPass123")
    private String password;

    @NotNull(message = "Role is required")
    @Schema(description = "User role", example = "PATIENT")
    private Role role;

    @Schema(description = "Whether the account is active", example = "true")
    private Boolean enabled;
}

