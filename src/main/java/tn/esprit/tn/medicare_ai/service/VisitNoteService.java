package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.VisitNote;

import java.util.List;

public interface VisitNoteService {
    VisitNote create(VisitNoteDTO dto, Long currentUserId, String currentRole);
    VisitNote getById(Long id, Long currentUserId, String currentRole);
    List<VisitNote> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole);
    VisitNote update(Long id, VisitNoteDTO dto, Long currentUserId, String currentRole);
    void delete(Long id, Long currentUserId, String currentRole);

    List<VisitNote> searchClinicalNotes(String patientKeyword, String doctorKeyword, String clinicalKeyword,
                                        Long currentUserId, String currentRole);
}