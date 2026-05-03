package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.MedicationScheduleRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationScheduleResponse;
import tn.esprit.tn.medicare_ai.entity.MedicationSchedule;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MedicationScheduleRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IMedicationScheduleService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class MedicationScheduleServiceImpl implements IMedicationScheduleService {
    private final MedicationScheduleRepository repo;
    private final UserRepository userRepository;

    @Override @Transactional
    public MedicationScheduleResponse create(MedicationScheduleRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        return toResponse(repo.save(MedicationSchedule.builder().medicineName(req.getMedicineName()).dosage(req.getDosage()).frequency(req.getFrequency()).startDate(req.getStartDate()).endDate(req.getEndDate()).recordDate(req.getRecordDate()).user(user).build()));
    }
    @Override public MedicationScheduleResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("MedicationSchedule not found: " + id))); }
    @Override public List<MedicationScheduleResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<MedicationScheduleResponse> getByUserId(Long userId) { return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public MedicationScheduleResponse update(Long id, MedicationScheduleRequest req) {
        MedicationSchedule ms = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("MedicationSchedule not found: " + id));
        ms.setMedicineName(req.getMedicineName()); ms.setDosage(req.getDosage()); ms.setFrequency(req.getFrequency());
        ms.setStartDate(req.getStartDate()); ms.setEndDate(req.getEndDate()); ms.setRecordDate(req.getRecordDate());
        return toResponse(repo.save(ms));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("MedicationSchedule not found: " + id); repo.deleteById(id); }
    private MedicationScheduleResponse toResponse(MedicationSchedule ms) {
        return MedicationScheduleResponse.builder().id(ms.getId()).medicineName(ms.getMedicineName()).dosage(ms.getDosage()).frequency(ms.getFrequency()).startDate(ms.getStartDate()).endDate(ms.getEndDate()).recordDate(ms.getRecordDate()).userId(ms.getUser().getId()).build();
    }
}
