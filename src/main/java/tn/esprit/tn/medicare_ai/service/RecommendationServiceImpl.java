package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.RecommendationRequest;
import tn.esprit.tn.medicare_ai.dto.response.RecommendationResponse;
import tn.esprit.tn.medicare_ai.entity.PregnancyTracking;
import tn.esprit.tn.medicare_ai.entity.Recommendation;
import tn.esprit.tn.medicare_ai.entity.RecommendationCategory;
import tn.esprit.tn.medicare_ai.repository.PregnancyTrackingRepository;
import tn.esprit.tn.medicare_ai.repository.RecommendationRepository;
import tn.esprit.tn.medicare_ai.service.IRecommendationService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class RecommendationServiceImpl implements IRecommendationService {
    private final RecommendationRepository repo;
    private final PregnancyTrackingRepository trackingRepository;

    @Override @Transactional
    public RecommendationResponse create(RecommendationRequest req) {
        PregnancyTracking pt = trackingRepository.findById(req.getPregnancyTrackingId()).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + req.getPregnancyTrackingId()));
        return toResponse(repo.save(Recommendation.builder().description(req.getDescription()).goal(req.getGoal()).category(req.getCategory()).pregnancyTracking(pt).build()));
    }
    @Override public RecommendationResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Recommendation not found: " + id))); }
    @Override public List<RecommendationResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<RecommendationResponse> getByPregnancyTrackingId(Long ptId) { return repo.findByPregnancyTrackingId(ptId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<RecommendationResponse> getByPregnancyTrackingIdAndCategory(Long ptId, RecommendationCategory cat) { return repo.findByPregnancyTrackingIdAndCategory(ptId, cat).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public RecommendationResponse update(Long id, RecommendationRequest req) {
        Recommendation r = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Recommendation not found: " + id));
        r.setDescription(req.getDescription()); r.setGoal(req.getGoal()); r.setCategory(req.getCategory());
        return toResponse(repo.save(r));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("Recommendation not found: " + id); repo.deleteById(id); }
    private RecommendationResponse toResponse(Recommendation r) {
        return RecommendationResponse.builder().id(r.getId()).description(r.getDescription()).goal(r.getGoal()).category(r.getCategory()).pregnancyTrackingId(r.getPregnancyTracking().getId()).build();
    }
}
