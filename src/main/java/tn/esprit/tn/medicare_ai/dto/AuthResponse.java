package tn.esprit.tn.medicare_ai.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {
}
