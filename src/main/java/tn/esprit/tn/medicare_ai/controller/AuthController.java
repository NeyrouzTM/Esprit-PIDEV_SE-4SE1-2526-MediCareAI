package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import tn.esprit.tn.medicare_ai.dto.AuthResponse;
import tn.esprit.tn.medicare_ai.dto.LoginRequest;
import tn.esprit.tn.medicare_ai.dto.PhysicianRecommendationDto;
import tn.esprit.tn.medicare_ai.dto.RegisterRequest;
import tn.esprit.tn.medicare_ai.dto.UserIdResponse;
import tn.esprit.tn.medicare_ai.dto.UserResponse;
import tn.esprit.tn.medicare_ai.dto.UserUpdateRequest;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IAuthService;
import tn.esprit.tn.medicare_ai.service.PhysicianRecommendationService;

@Tag(name = "Authentication & Users", description = "Authentication and user directory endpoints")
@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final UserRepository userRepository;
    private final PhysicianRecommendationService physicianRecommendationService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Simple registration without email verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User created: " + saved.getEmail());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password, returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/user-id")
    @Operation(summary = "Current user id", description = "Returns id and email for the authenticated user")
    public ResponseEntity<UserIdResponse> getUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new UserIdResponse(user.getId(), user.getEmail()));
    }

    @GetMapping("/doctors")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Search doctors", description = "Search for doctors by name or email. Available to authenticated users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<UserResponse>> getDoctors(
            @Parameter(description = "Search query (name or email)")
            @RequestParam(required = false) String query,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(authService.getDoctors(query, pageable));
    }

    @GetMapping("/doctors/recommend")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Rank doctors by clinical keywords", description = "Matches keywords against doctor profile and visit-note SOAP text.")
    public ResponseEntity<List<PhysicianRecommendationDto>> recommendDoctors(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "15") int limit) {
        return ResponseEntity.ok(physicianRecommendationService.recommend(keywords, limit));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all users with filters", description = "Admin only: list all users with optional role and text search filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    public ResponseEntity<Page<UserResponse>> getUsers(
            @Parameter(description = "Search by full name or email", in = ParameterIn.QUERY)
            @RequestParam(required = false) String query,
            @Parameter(description = "Filter by role")
            @RequestParam(required = false) Role role,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(authService.getUsers(query, role, pageable));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user", description = "Admin only: update user information (name, email, password, role, status)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - email already in use or password too short"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(authService.updateUser(id, req));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete user", description = "Admin only: permanently delete a user account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
