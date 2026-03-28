package tn.esprit.tn.medicare_ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.UserIdResponse;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IAuthService;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User created: " + saved.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/user-id")
    public ResponseEntity<UserIdResponse> getUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new UserIdResponse(user.getId(), user.getEmail()));
    }
}