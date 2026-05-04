package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyTrackingRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyTrackingResponse;
import tn.esprit.tn.medicare_ai.entity.PregnancyTracking;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.PregnancyTrackingRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IPregnancyTrackingService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PregnancyTrackingServiceImpl implements IPregnancyTrackingService {
    private final PregnancyTrackingRepository repo;
    private final UserRepository userRepository;
    
    // Constants for pregnancy calculations
    private static final int PREGNANCY_DURATION_DAYS = 280;

    @Override @Transactional
    public PregnancyTrackingResponse create(PregnancyTrackingRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        
        // Auto-calculate current week and due date if not provided
        Integer currentWeek = req.getCurrentWeek() != null ? req.getCurrentWeek() : calculateCurrentWeek(req.getStartDate());
        LocalDate dueDate = req.getDueDate() != null ? req.getDueDate() : calculateDueDate(req.getStartDate());
        
        return toResponse(repo.save(PregnancyTracking.builder()
                .startDate(req.getStartDate())
                .currentWeek(currentWeek)
                .notes(req.getNotes())
                .dueDate(dueDate)
                .user(user)
                .build()));
    }
    
    @Override public PregnancyTrackingResponse getById(Long id) { 
        return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + id))); 
    }
    
    @Override public List<PregnancyTrackingResponse> getAll() { 
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); 
    }
    
    @Override public List<PregnancyTrackingResponse> getByUserId(Long userId) { 
        return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); 
    }
    
    @Override @Transactional
    public PregnancyTrackingResponse update(Long id, PregnancyTrackingRequest req) {
        PregnancyTracking pt = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + id));
        pt.setStartDate(req.getStartDate());
        
        // Auto-update current week and due date
        pt.setCurrentWeek(calculateCurrentWeek(req.getStartDate()));
        pt.setDueDate(calculateDueDate(req.getStartDate()));
        pt.setNotes(req.getNotes());
        
        return toResponse(repo.save(pt));
    }
    
    @Override @Transactional
    public void delete(Long id) { 
        if (!repo.existsById(id)) throw new EntityNotFoundException("PregnancyTracking not found: " + id); 
        repo.deleteById(id); 
    }
    
    /**
     * Calculate the current pregnancy week based on the Last Menstrual Period (LMP)
     * Uses ChronoUnit.WEEKS to calculate weeks between LMP and today
     * @param lastMenstrualPeriod the start date (LMP)
     * @return the current pregnancy week (1-42)
     */
    @Override
    public Integer calculateCurrentWeek(LocalDate lastMenstrualPeriod) {
        if (lastMenstrualPeriod == null) {
            throw new IllegalArgumentException("Last menstrual period date cannot be null");
        }
        if (lastMenstrualPeriod.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Last menstrual period cannot be in the future");
        }
        
        long weeksDifference = ChronoUnit.WEEKS.between(lastMenstrualPeriod, LocalDate.now());
        int currentWeek = (int) weeksDifference + 1;
        
        // Pregnancy normally lasts 40 weeks, but can extend to 42 weeks
        return Math.min(Math.max(currentWeek, 1), 42);
    }
    
    /**
     * Calculate the due date (Expected Date of Delivery)
     * Standard pregnancy duration is 280 days (40 weeks) from LMP
     * @param lastMenstrualPeriod the start date (LMP)
     * @return the expected due date
     */
    @Override
    public LocalDate calculateDueDate(LocalDate lastMenstrualPeriod) {
        if (lastMenstrualPeriod == null) {
            throw new IllegalArgumentException("Last menstrual period date cannot be null");
        }
        return lastMenstrualPeriod.plusDays(PREGNANCY_DURATION_DAYS);
    }
    
    private PregnancyTrackingResponse toResponse(PregnancyTracking pt) {
        return PregnancyTrackingResponse.builder()
                .id(pt.getId())
                .startDate(pt.getStartDate())
                .currentWeek(pt.getCurrentWeek())
                .notes(pt.getNotes())
                .dueDate(pt.getDueDate())
                .userId(pt.getUser().getId())
                .build();
    }
}
