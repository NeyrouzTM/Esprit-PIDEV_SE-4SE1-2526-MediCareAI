package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.entity.User;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}