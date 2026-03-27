package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import java.util.List;

public interface VisitNoteService {
    VisitNote create(VisitNoteDTO dto);
    VisitNote getById(Long id);
    List<VisitNote> getByMedicalRecordId(Long medicalRecordId);
    VisitNote update(Long id, VisitNoteDTO dto);
    void delete(Long id);
}