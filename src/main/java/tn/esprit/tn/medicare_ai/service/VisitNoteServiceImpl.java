package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.VisitNoteDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitNoteServiceImpl implements VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    @Override
    public VisitNote create(VisitNoteDTO dto, Long currentUserId, String currentRole) {
        if (dto.getMedicalRecordId() == null)
            throw new IllegalArgumentException("Medical record ID required");
        if (dto.getDoctorId() == null)
            throw new IllegalArgumentException("Doctor ID required");

        MedicalRecord record = medicalRecordRepository
                .findById(dto.getMedicalRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));

        ensureCanAccessRecord(record, currentUserId, currentRole);

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

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
    public VisitNote getById(Long id, Long currentUserId, String currentRole) {
        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visit note not found"));
        ensureCanAccessRecord(visitNote.getMedicalRecord(), currentUserId, currentRole);
        return visitNote;
    }

    @Override
    public List<VisitNote> getByMedicalRecordId(Long medicalRecordId, Long currentUserId, String currentRole) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        ensureCanAccessRecord(record, currentUserId, currentRole);
        return visitNoteRepository.findByMedicalRecordId(medicalRecordId);
    }

    @Override
    public VisitNote update(Long id, VisitNoteDTO dto, Long currentUserId, String currentRole) {
        VisitNote visitNote = getById(id, currentUserId, currentRole);
        if (visitNote.isFinalized())
            throw new IllegalArgumentException("Cannot update a finalized visit note");
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
    public void delete(Long id, Long currentUserId, String currentRole) {
        VisitNote visitNote = getById(id, currentUserId, currentRole);
        if (visitNote.isFinalized())
            throw new IllegalArgumentException("Cannot delete a finalized visit note");
        visitNoteRepository.delete(visitNote);
    }

    @Override
    public List<VisitNote> searchClinicalNotes(String patientKeyword, String doctorKeyword, String clinicalKeyword,
                                               Long currentUserId, String currentRole) {
        List<VisitNote> allMatches = visitNoteRepository.searchClinicalNotes(
                normalizeKeyword(patientKeyword),
                normalizeKeyword(doctorKeyword),
                normalizeKeyword(clinicalKeyword)
        );

        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            return allMatches;
        }

        if ("PATIENT".equals(currentRole)) {
            return allMatches.stream()
                    .filter(vn -> vn.getMedicalRecord() != null
                            && vn.getMedicalRecord().getPatient() != null
                            && currentUserId.equals(vn.getMedicalRecord().getPatient().getId()))
                    .collect(Collectors.toList());
        }

        throw new UnauthorizedActionException("You are not allowed to search visit notes");
    }

    private void ensureCanAccessRecord(MedicalRecord record, Long currentUserId, String currentRole) {
        if ("ADMIN".equals(currentRole) || "DOCTOR".equals(currentRole)) {
            return;
        }
        if ("PATIENT".equals(currentRole) && record.getPatient().getId().equals(currentUserId)) {
            return;
        }
        throw new UnauthorizedActionException("You are not allowed to access this visit note data");
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return keyword.trim();
    }
}