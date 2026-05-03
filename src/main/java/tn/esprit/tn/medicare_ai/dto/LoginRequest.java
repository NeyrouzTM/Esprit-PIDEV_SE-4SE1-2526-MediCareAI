package tn.esprit.tn.medicare_ai.dto;

public record LoginRequest(
        String email,
        String password
) {
    // Use record accessors: email() and password()
}
