package tn.esprit.tn.medicare_ai.service.implementation;


import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Subscription;
import tn.esprit.tn.medicare_ai.entity.SubscriptionPlan;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.SubscriptionRepository;
import tn.esprit.tn.medicare_ai.repository.SubscriptionPlanRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   SubscriptionPlanRepository planRepository,
                                   UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO createSubscription(Long userId, Long planId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur avec ID " + userId + " non trouvé"));

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan d'abonnement avec ID " + planId + " non trouvé"));

        // Vérification métier importante
        if (!"PATIENT".equals(user.getRole().name())) {
            throw new IllegalArgumentException("Seuls les patients peuvent souscrire à un abonnement");
        }

        // Vérifier qu'il n'a pas déjà un abonnement actif
        List<Subscription> activeSubs = subscriptionRepository.findByUserIdAndStatus(userId, Subscription.SubscriptionStatus.ACTIVE);
        if (!activeSubs.isEmpty()) {
            throw new IllegalStateException("L'utilisateur a déjà un abonnement actif");
        }

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<SubscriptionResponseDTO> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO getSubscriptionById(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abonnement non trouvé"));
        return mapToResponseDTO(sub);
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO renewSubscription(Long subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Abonnement non trouvé"));

        if (sub.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Impossible de renouveler un abonnement non actif");
        }

        // Prolonger la durée
        LocalDateTime newEndDate = sub.getEndDate().plusDays(sub.getPlan().getDurationDays());
        sub.setEndDate(newEndDate);

        Subscription updated = subscriptionRepository.save(sub);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long subscriptionId, Long userId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Abonnement non trouvé"));

        if (!sub.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous ne pouvez annuler que vos propres abonnements");
        }

        sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(sub);
    }

    @Override
    @Transactional
    public void deleteSubscription(Long subscriptionId, Long userId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Abonnement non trouvé"));

        if (!sub.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous ne pouvez supprimer que vos propres abonnements");
        }

        subscriptionRepository.delete(sub);
    }

    private SubscriptionResponseDTO mapToResponseDTO(Subscription sub) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(sub.getId());
        dto.setUserId(sub.getUser().getId());
        dto.setPlanId(sub.getPlan().getId());
        dto.setPlanName(sub.getPlan().getName());
        dto.setStartDate(sub.getStartDate());
        dto.setEndDate(sub.getEndDate());
        dto.setStatus(sub.getStatus().name());
        dto.setActiveNow(sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE
                && LocalDateTime.now().isBefore(sub.getEndDate()));
        return dto;
    }
}