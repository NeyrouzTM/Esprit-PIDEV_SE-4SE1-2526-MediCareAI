package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.ActivityRequest;
import tn.esprit.tn.medicare_ai.dto.response.ActivityResponse;
import tn.esprit.tn.medicare_ai.entity.Activity;
import tn.esprit.tn.medicare_ai.entity.PregnancyTracking;
import tn.esprit.tn.medicare_ai.repository.ActivityRepository;
import tn.esprit.tn.medicare_ai.repository.PregnancyTrackingRepository;
import tn.esprit.tn.medicare_ai.service.IActivityService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public  class ActivityServiceImpl implements IActivityService {
    private final ActivityRepository repo;
    private final PregnancyTrackingRepository trackingRepository;

    @Override @Transactional
    public ActivityResponse create(ActivityRequest req) {
        PregnancyTracking pt = trackingRepository.findById(req.getPregnancyTrackingId()).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + req.getPregnancyTrackingId()));
        return toResponse(repo.save(Activity.builder().type(req.getType()).duration(req.getDuration()).benefit(req.getBenefit()).pregnancyTracking(pt).build()));
    }
    @Override public ActivityResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found: " + id))); }
    @Override public List<ActivityResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<ActivityResponse> getByPregnancyTrackingId(Long ptId) { return repo.findByPregnancyTrackingId(ptId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public ActivityResponse update(Long id, ActivityRequest req) {
        Activity a = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found: " + id));
        a.setType(req.getType()); a.setDuration(req.getDuration()); a.setBenefit(req.getBenefit());
        return toResponse(repo.save(a));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("Activity not found: " + id); repo.deleteById(id); }
    private ActivityResponse toResponse(Activity a) {
        return ActivityResponse.builder().id(a.getId()).type(a.getType()).duration(a.getDuration()).benefit(a.getBenefit()).pregnancyTrackingId(a.getPregnancyTracking().getId()).build();
    }
}
