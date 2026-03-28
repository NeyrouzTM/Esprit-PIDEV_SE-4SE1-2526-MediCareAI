package tn.esprit.tn.medicare_ai.repository.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.tn.medicare_ai.entity.Symptom;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    boolean existsByName(String name);
}
