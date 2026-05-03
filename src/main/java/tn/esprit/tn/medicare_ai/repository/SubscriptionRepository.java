package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.Subscription;
import tn.esprit.tn.medicare_ai.entity.Subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByPlanId(Long planId);

    List<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // Méthode importante pour la suppression de plan
    long countByPlanId(Long planId);

    // Vérifier si un utilisateur a un abonnement actif
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // Trouver les abonnements expirés pour le scheduler
    @Query("SELECT s FROM Subscription s WHERE s.endDate <= CURRENT_TIMESTAMP AND s.status != 'EXPIRED'")
    List<Subscription> findExpiredSubscriptions();
}
