package tn.esprit.tn.medicare_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import java.util.List;

public interface VisitNoteRepository
        extends JpaRepository<VisitNote, Long> {
    List<VisitNote> findByMedicalRecordId(Long medicalRecordId);
}