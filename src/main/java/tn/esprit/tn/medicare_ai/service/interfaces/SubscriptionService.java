package tn.esprit.tn.medicare_ai.service.interfaces;



import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import java.util.List;

public interface SubscriptionService {

    SubscriptionResponseDTO createSubscription(Long userId, Long planId);

    List<SubscriptionResponseDTO> getSubscriptionsByUser(Long userId);

    SubscriptionResponseDTO getSubscriptionById(Long id);

    SubscriptionResponseDTO renewSubscription(Long subscriptionId);

    void cancelSubscription(Long subscriptionId, Long userId);

    void deleteSubscription(Long subscriptionId, Long userId);   // Suppression physique (optionnel)

    boolean hasActiveSubscription(Long userId);
}
