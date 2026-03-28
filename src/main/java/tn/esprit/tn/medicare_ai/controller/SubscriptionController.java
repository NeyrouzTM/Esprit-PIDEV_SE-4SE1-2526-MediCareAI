package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
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
}
