package tn.esprit.tn.medicare_ai.dto;

import tn.esprit.tn.medicare_ai.entity.Role;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        Role role
) {
}
