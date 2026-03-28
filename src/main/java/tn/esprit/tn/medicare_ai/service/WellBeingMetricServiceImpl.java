package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.WellBeingMetricRequest;
import tn.esprit.tn.medicare_ai.dto.response.WellBeingMetricResponse;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.WellBeingMetric;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.WellBeingMetricRepository;
import tn.esprit.tn.medicare_ai.service.IWellBeingMetricService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class WellBeingMetricServiceImpl implements IWellBeingMetricService {
    private final WellBeingMetricRepository repo;
    private final UserRepository userRepository;

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
}
