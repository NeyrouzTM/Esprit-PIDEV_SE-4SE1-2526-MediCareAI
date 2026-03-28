package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tn.esprit.tn.medicare_ai.dto.request.CreateUserRequest;
import tn.esprit.tn.medicare_ai.dto.request.UpdateUserRequest;
import tn.esprit.tn.medicare_ai.dto.response.UserResponse;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already used");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(request.getEnabled() == null || request.getEnabled())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> listUsers(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return userRepository.findAll(pageable).map(this::toResponse);
        }

        return userRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already used");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
}

