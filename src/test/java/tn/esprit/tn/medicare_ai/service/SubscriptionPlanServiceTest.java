package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.SubscriptionPlanRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionPlanResponseDTO;
import tn.esprit.tn.medicare_ai.entity.SubscriptionPlan;
import tn.esprit.tn.medicare_ai.repository.SubscriptionPlanRepository;
import tn.esprit.tn.medicare_ai.repository.SubscriptionRepository;
import tn.esprit.tn.medicare_ai.service.implementation.SubscriptionPlanServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceTest {

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionPlanServiceImpl planService;

    @Test
    @DisplayName("createPlan: valid request creates plan")
    void createPlan_validRequest_createsPlan() {
        SubscriptionPlanRequestDTO request = SubscriptionPlanRequestDTO.builder()
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        SubscriptionPlan savedPlan = SubscriptionPlan.builder()
                .id(1L)
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        when(planRepository.save(any(SubscriptionPlan.class))).thenReturn(savedPlan);

        SubscriptionPlanResponseDTO result = planService.createPlan(request);

        assertEquals("Basic Plan", result.getName());
        assertEquals(10.0, result.getPrice());
        verify(planRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("getAllPlans: returns all plans")
    void getAllPlans_returnsAllPlans() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        when(planRepository.findAll()).thenReturn(List.of(plan));

        List<SubscriptionPlanResponseDTO> result = planService.getAllPlans();

        assertEquals(1, result.size());
        assertEquals("Basic Plan", result.get(0).getName());
    }

    @Test
    @DisplayName("getPlanById: valid id returns plan")
    void getPlanById_validId_returnsPlan() {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        SubscriptionPlanResponseDTO result = planService.getPlanById(1L);

        assertEquals("Basic Plan", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getPlanById: invalid id throws exception")
    void getPlanById_invalidId_throwsException() {
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> planService.getPlanById(1L));
    }
}
