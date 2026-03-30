package tn.esprit.tn.medicare_ai.dto;

import tn.esprit.tn.medicare_ai.entity.Role;

public record UserUpdateRequest(
        String fullName,
        String email,
        String password,
        Role role,
        Boolean enabled
) {
}

