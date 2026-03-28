package tn.esprit.tn.medicare_ai.repository.chatbot;


import tn.esprit.tn.medicare_ai.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    boolean existsByName(String name);

}