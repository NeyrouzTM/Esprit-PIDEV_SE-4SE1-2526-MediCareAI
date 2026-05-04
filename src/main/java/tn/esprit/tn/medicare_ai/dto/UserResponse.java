package tn.esprit.tn.medicare_ai.dto;

import tn.esprit.tn.medicare_ai.entity.Role;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean enabled,
        Long specialtyId,
        String clinicalDepartment,
        String clinicalKeywords
) {
    public UserResponse(Long id, String fullName, String email, Role role, boolean enabled) {
        this(id, fullName, email, role, enabled, null, null, null);
    }
}

