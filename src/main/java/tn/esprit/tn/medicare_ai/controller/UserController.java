package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.esprit.tn.medicare_ai.dto.request.CreateUserRequest;
import tn.esprit.tn.medicare_ai.dto.request.UpdateUserRequest;
import tn.esprit.tn.medicare_ai.dto.response.UserResponse;
import tn.esprit.tn.medicare_ai.exception.ErrorResponse;
import tn.esprit.tn.medicare_ai.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User management endpoints (admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns paginated users with optional keyword filter on full name/email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched"),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<Page<UserResponse>> listUsers(
            @Parameter(description = "Search keyword for full name or email", example = "ahmed")
            @RequestParam(required = false) String keyword,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(keyword, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Returns one user by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User identifier", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user profile and role. Password updates only when provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User identifier", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User identifier", example = "1") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

