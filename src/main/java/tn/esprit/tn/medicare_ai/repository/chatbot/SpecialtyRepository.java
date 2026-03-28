package tn.esprit.tn.medicare_ai.repository.chatbot;

import tn.esprit.tn.medicare_ai.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    boolean existsByName(String name);
}