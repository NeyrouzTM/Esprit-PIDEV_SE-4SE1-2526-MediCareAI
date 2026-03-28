package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitNoteServiceImpl implements VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Override
    public VisitNote create(VisitNoteDTO dto) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medical record not found"));

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        VisitNote visitNote = VisitNote.builder()
                .medicalRecord(record)
                .doctor(doctor)
                .visitDate(dto.getVisitDate())
                .subjective(dto.getSubjective())
                .objective(dto.getObjective())
                .assessment(dto.getAssessment())
                .plan(dto.getPlan())
                .finalized(false)
                .build();

        return visitNoteRepository.save(visitNote);
    }

    @Override
    public VisitNote getById(Long id) {
        return visitNoteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Visit note not found"));
    }

    @Override
    public List<VisitNote> getByMedicalRecordId(Long medicalRecordId) {
        return visitNoteRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public VisitNote update(Long id, VisitNoteDTO dto) {
        VisitNote visitNote = getById(id);
        if (visitNote.isFinalized())
            throw new IllegalArgumentException(
                    "Cannot update a finalized visit note");
        if (dto.getSubjective() != null)
            visitNote.setSubjective(dto.getSubjective());
        if (dto.getObjective() != null)
            visitNote.setObjective(dto.getObjective());
        if (dto.getAssessment() != null)
            visitNote.setAssessment(dto.getAssessment());
        if (dto.getPlan() != null)
            visitNote.setPlan(dto.getPlan());
        if (dto.getVisitDate() != null)
            visitNote.setVisitDate(dto.getVisitDate());
        visitNote.setFinalized(dto.isFinalized());
        return visitNoteRepository.save(visitNote);
    }

    @Override
    public void delete(Long id) {
        VisitNote visitNote = getById(id);
        if (visitNote.isFinalized())
            throw new IllegalArgumentException(
                    "Cannot delete a finalized visit note");
        visitNoteRepository.delete(visitNote);
    }
}