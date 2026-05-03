package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    // Recherche par nom (optionnel)
    boolean existsByName(String name);
}
