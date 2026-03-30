package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.User;

import java.util.List;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
    List<UserResponse> getUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest req);
    void deleteUser(Long id);
}