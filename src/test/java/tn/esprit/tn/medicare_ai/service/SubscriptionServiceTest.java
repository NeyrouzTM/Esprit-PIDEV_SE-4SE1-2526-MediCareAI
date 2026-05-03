package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.Subscription;
import tn.esprit.tn.medicare_ai.entity.SubscriptionPlan;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.SubscriptionRepository;
import tn.esprit.tn.medicare_ai.repository.SubscriptionPlanRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.SubscriptionServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    @DisplayName("createSubscription: valid request creates subscription")
    void createSubscription_validRequest_createsSubscription() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.PATIENT);

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(1L);
        plan.setDurationDays(30);

        Subscription savedSubscription = Subscription.builder()
                .id(1L)
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUserIdAndStatus(1L, Subscription.SubscriptionStatus.ACTIVE)).thenReturn(List.of());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);

        SubscriptionResponseDTO result = subscriptionService.createSubscription(1L, 1L);

        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getPlanId());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("getSubscriptionsByUser: returns subscriptions for user")
    void getSubscriptionsByUser_returnsSubscriptions() {
        User user = new User();
        user.setId(1L);

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(1L);
        plan.setName("Basic Plan");

        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepository.findByUserId(1L)).thenReturn(List.of(subscription));

        List<SubscriptionResponseDTO> result = subscriptionService.getSubscriptionsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("getSubscriptionById: valid id returns subscription")
    void getSubscriptionById_validId_returnsSubscription() {
        User user = new User();
        user.setId(1L);

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(1L);
        plan.setName("Basic Plan");

        Subscription subscription = Subscription.builder()
                .id(1L)
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        SubscriptionResponseDTO result = subscriptionService.getSubscriptionById(1L);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    @DisplayName("getSubscriptionById: invalid id throws exception")
    void getSubscriptionById_invalidId_throwsException() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> subscriptionService.getSubscriptionById(1L));
    }
}


