package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.MedicationReminderRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationReminderResponse;
import tn.esprit.tn.medicare_ai.entity.MedicationReminder;
import tn.esprit.tn.medicare_ai.entity.MedicationSchedule;
import tn.esprit.tn.medicare_ai.repository.MedicationReminderRepository;
import tn.esprit.tn.medicare_ai.repository.MedicationScheduleRepository;
import tn.esprit.tn.medicare_ai.service.IMedicationReminderService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class MedicationReminderServiceImpl implements IMedicationReminderService {
    private final MedicationReminderRepository repo;
    private final MedicationScheduleRepository scheduleRepository;

    @Override @Transactional
    public MedicationReminderResponse create(MedicationReminderRequest req) {
        MedicationSchedule schedule = scheduleRepository.findById(req.getMedicationScheduleId()).orElseThrow(() -> new EntityNotFoundException("MedicationSchedule not found: " + req.getMedicationScheduleId()));
        return toResponse(repo.save(MedicationReminder.builder().time(req.getTime()).message(req.getMessage()).medicationSchedule(schedule).build()));
    }
    @Override public MedicationReminderResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("MedicationReminder not found: " + id))); }
    @Override public List<MedicationReminderResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<MedicationReminderResponse> getByScheduleId(Long scheduleId) { return repo.findByMedicationScheduleId(scheduleId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public MedicationReminderResponse update(Long id, MedicationReminderRequest req) {
        MedicationReminder r = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("MedicationReminder not found: " + id));
        r.setTime(req.getTime()); r.setMessage(req.getMessage());
        return toResponse(repo.save(r));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("MedicationReminder not found: " + id); repo.deleteById(id); }
    private MedicationReminderResponse toResponse(MedicationReminder r) {
        return MedicationReminderResponse.builder().id(r.getId()).time(r.getTime()).message(r.getMessage()).medicationScheduleId(r.getMedicationSchedule().getId()).build();
    }
}
