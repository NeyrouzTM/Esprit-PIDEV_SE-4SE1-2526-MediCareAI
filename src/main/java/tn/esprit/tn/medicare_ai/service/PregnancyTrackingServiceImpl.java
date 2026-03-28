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
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PregnancyTrackingServiceImpl implements IPregnancyTrackingService {
    private final PregnancyTrackingRepository repo;
    private final UserRepository userRepository;

    @Override @Transactional
    public PregnancyTrackingResponse create(PregnancyTrackingRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        return toResponse(repo.save(PregnancyTracking.builder().startDate(req.getStartDate()).currentWeek(req.getCurrentWeek()).notes(req.getNotes()).dueDate(req.getDueDate()).user(user).build()));
    }
    @Override public PregnancyTrackingResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + id))); }
    @Override public List<PregnancyTrackingResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<PregnancyTrackingResponse> getByUserId(Long userId) { return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public PregnancyTrackingResponse update(Long id, PregnancyTrackingRequest req) {
        PregnancyTracking pt = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + id));
        pt.setStartDate(req.getStartDate()); pt.setCurrentWeek(req.getCurrentWeek()); pt.setNotes(req.getNotes()); pt.setDueDate(req.getDueDate());
        return toResponse(repo.save(pt));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("PregnancyTracking not found: " + id); repo.deleteById(id); }
    private PregnancyTrackingResponse toResponse(PregnancyTracking pt) {
        return PregnancyTrackingResponse.builder().id(pt.getId()).startDate(pt.getStartDate()).currentWeek(pt.getCurrentWeek()).notes(pt.getNotes()).dueDate(pt.getDueDate()).userId(pt.getUser().getId()).build();
    }
}
