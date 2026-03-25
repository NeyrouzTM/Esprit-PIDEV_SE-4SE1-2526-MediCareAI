package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.tn.medicare_ai.entity.DrugInteraction;

import java.util.List;

public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, Long> {

    List<DrugInteraction> findByMedicineAIdOrMedicineBId(Long medicineAId, Long medicineBId);

    @Query("""
            select d from DrugInteraction d
            where (d.medicineA.id in :medicineIds and d.medicineB.id in :medicineIds)
            """)
    List<DrugInteraction> findInteractionsForMedicines(@Param("medicineIds") List<Long> medicineIds);
}
