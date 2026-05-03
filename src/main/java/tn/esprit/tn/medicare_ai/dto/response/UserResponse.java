package tn.esprit.tn.medicare_ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.tn.medicare_ai.entity.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User response payload")
public class UserResponse {

    @Schema(description = "User identifier", example = "42")
    private Long id;

    @Schema(description = "Full name", example = "Ahmed Ben Ali")
    private String fullName;

    @Schema(description = "Email", example = "ahmed@example.com")
    private String email;

    @Schema(description = "Assigned role", example = "PHARMACIST")
    private Role role;

    @Schema(description = "Whether the account is enabled", example = "true")
    private boolean enabled;
}

