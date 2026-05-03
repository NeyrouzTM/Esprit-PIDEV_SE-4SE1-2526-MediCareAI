package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.WellBeingMetricRequest;
import tn.esprit.tn.medicare_ai.dto.response.WellBeingMetricResponse;
import tn.esprit.tn.medicare_ai.entity.MedicationReminder;
import tn.esprit.tn.medicare_ai.entity.MedicationSchedule;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.WellBeingMetric;
import tn.esprit.tn.medicare_ai.repository.MedicationReminderRepository;
import tn.esprit.tn.medicare_ai.repository.MedicationScheduleRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.WellBeingMetricRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
@EnableScheduling
public class WellBeingMetricServiceImpl implements IWellBeingMetricService {
    private final WellBeingMetricRepository repo;
    private final UserRepository userRepository;
    private final WellBeingMetricRepository metricRepo;
    private final MedicationReminderRepository reminderRepo;
    private final MedicationScheduleRepository medicationScheduleRepository;


    @Override @Transactional
    public WellBeingMetricResponse create(WellBeingMetricRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        return toResponse(repo.save(WellBeingMetric.builder().level(req.getLevel()).frequency(req.getFrequency()).startDate(req.getStartDate()).endDate(req.getEndDate()).user(user).build()));
    }
    @Override public WellBeingMetricResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("WellBeingMetric not found: " + id))); }
    @Override public List<WellBeingMetricResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<WellBeingMetricResponse> getByUserId(Long userId) { return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public WellBeingMetricResponse update(Long id, WellBeingMetricRequest req) {
        WellBeingMetric w = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("WellBeingMetric not found: " + id));
        w.setLevel(req.getLevel()); w.setFrequency(req.getFrequency()); w.setStartDate(req.getStartDate()); w.setEndDate(req.getEndDate());
        return toResponse(repo.save(w));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("WellBeingMetric not found: " + id); repo.deleteById(id); }
    private WellBeingMetricResponse toResponse(WellBeingMetric w) {
        return WellBeingMetricResponse.builder().id(w.getId()).level(w.getLevel()).frequency(w.getFrequency()).startDate(w.getStartDate()).endDate(w.getEndDate()).userId(w.getUser().getId()).build();
    }


    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    @Override
    public void detectLowWellbeing() {

        List<Long> userIds = metricRepo.findAllUserIds();

        for (Long userId : userIds) {

            List<WellBeingMetric> last3 =
                    metricRepo.findTop3ByUser_IdOrderByStartDateDesc(userId);

            if (last3.size() == 3 &&
                    last3.stream().allMatch(m -> m.getLevel().equalsIgnoreCase("LOW"))) {

                User user = userRepository.findById(userId).orElseThrow(() -> 
                    new EntityNotFoundException("User not found: " + userId));

                // Create or get existing medication schedule for low wellbeing alert
                MedicationSchedule schedule = MedicationSchedule.builder()
                    .medicineName("Consult Doctor")
                    .dosage("Low wellbeing detected")
                    .frequency("As needed")
                    .startDate(java.time.LocalDate.now())
                    .recordDate(java.time.LocalDate.now())
                    .user(user)
                    .build();

                MedicationSchedule savedSchedule = medicationScheduleRepository.save(schedule);

                // Create reminder
                MedicationReminder alert = MedicationReminder.builder()
                    .message("Low wellbeing detected - Please consult a doctor")
                    .time(LocalTime.now())
                    .medicationSchedule(savedSchedule)
                    .build();

                reminderRepo.save(alert);
            }
        }
    }




}
