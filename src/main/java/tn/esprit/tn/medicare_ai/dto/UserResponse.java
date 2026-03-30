package tn.esprit.tn.medicare_ai.dto;

import tn.esprit.tn.medicare_ai.entity.Role;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean enabled
) {
}

