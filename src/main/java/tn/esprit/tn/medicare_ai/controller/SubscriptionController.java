package tn.esprit.tn.medicare_ai.controller;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(
            @RequestParam Long userId,
            @RequestParam Long planId) {

        SubscriptionResponseDTO created = subscriptionService.createSubscription(userId, planId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<SubscriptionResponseDTO> renewSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable Long id,
            @RequestParam Long userId) {

        subscriptionService.cancelSubscription(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/has-active")
    public ResponseEntity<Map<String, Boolean>> hasActiveSubscription() {
        User currentUser = getCurrentUser();
        boolean hasActive = subscriptionService.hasActiveSubscription(currentUser.getId());
        return ResponseEntity.ok(Map.of("hasActive", hasActive));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + email));
    }
}
