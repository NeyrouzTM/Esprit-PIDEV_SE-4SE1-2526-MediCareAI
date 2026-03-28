package tn.esprit.tn.medicare_ai.service.interfaces;



import tn.esprit.tn.medicare_ai.dto.request.SubscriptionPlanRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionPlanResponseDTO;
import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanResponseDTO createPlan(SubscriptionPlanRequestDTO dto);
    List<SubscriptionPlanResponseDTO> getAllPlans();
    SubscriptionPlanResponseDTO getPlanById(Long id);
    SubscriptionPlanResponseDTO updatePlan(Long id, SubscriptionPlanRequestDTO dto);
    void deletePlan(Long id);
}
