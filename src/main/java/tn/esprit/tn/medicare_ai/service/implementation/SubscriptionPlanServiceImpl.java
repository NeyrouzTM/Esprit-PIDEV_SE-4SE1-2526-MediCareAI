package tn.esprit.tn.medicare_ai.service.implementation;
import tn.esprit.tn.medicare_ai.dto.request.SubscriptionPlanRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionPlanResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Subscription;
import tn.esprit.tn.medicare_ai.entity.SubscriptionPlan;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.SubscriptionPlanRepository;
import tn.esprit.tn.medicare_ai.repository.SubscriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionPlanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionPlanServiceImpl(SubscriptionPlanRepository planRepository,
                                       SubscriptionRepository subscriptionRepository) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    @Transactional
    public SubscriptionPlanResponseDTO createPlan(SubscriptionPlanRequestDTO dto) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .durationDays(dto.getDurationDays())
                .description(dto.getDescription())
                .build();

        SubscriptionPlan saved = planRepository.save(plan);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<SubscriptionPlanResponseDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanResponseDTO getPlanById(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan avec ID " + id + " non trouvé"));
        return mapToResponseDTO(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponseDTO updatePlan(Long id, SubscriptionPlanRequestDTO dto) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan avec ID " + id + " non trouvé"));

        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setDurationDays(dto.getDurationDays());
        plan.setDescription(dto.getDescription());

        SubscriptionPlan updated = planRepository.save(plan);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan avec ID " + id + " non trouvé"));

        // Vérification importante : ne pas supprimer si des abonnements sont liés
        long count = subscriptionRepository.countByPlanId(id);
        if (count > 0) {
            throw new IllegalStateException("Impossible de supprimer ce plan. "
                    + count + " abonnement(s) sont liés à ce plan.");
        }

        planRepository.delete(plan);
    }

    private SubscriptionPlanResponseDTO mapToResponseDTO(SubscriptionPlan plan) {
        return SubscriptionPlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .description(plan.getDescription())
                .build();
    }
}

