package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyCheckupRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyCheckupResponse;
import tn.esprit.tn.medicare_ai.entity.PregnancyCheckup;
import tn.esprit.tn.medicare_ai.entity.PregnancyTracking;
import tn.esprit.tn.medicare_ai.repository.PregnancyCheckupRepository;
import tn.esprit.tn.medicare_ai.repository.PregnancyTrackingRepository;
import tn.esprit.tn.medicare_ai.service.IPregnancyCheckupService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PregnancyCheckupServiceImpl implements IPregnancyCheckupService {
    private final PregnancyCheckupRepository repo;
    private final PregnancyTrackingRepository trackingRepository;

    @Override @Transactional
    public PregnancyCheckupResponse create(PregnancyCheckupRequest req) {
        PregnancyTracking pt = trackingRepository.findById(req.getPregnancyTrackingId()).orElseThrow(() -> new EntityNotFoundException("PregnancyTracking not found: " + req.getPregnancyTrackingId()));
        return toResponse(repo.save(PregnancyCheckup.builder().date(req.getDate()).observation(req.getObservation()).weightKg(req.getWeightKg()).symptoms(req.getSymptoms()).fetalMovements(req.getFetalMovements()).pregnancyTracking(pt).build()));
    }
    @Override public PregnancyCheckupResponse getById(Long id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyCheckup not found: " + id))); }
    @Override public List<PregnancyCheckupResponse> getAll() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<PregnancyCheckupResponse> getByPregnancyTrackingId(Long ptId) { return repo.findByPregnancyTrackingId(ptId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public PregnancyCheckupResponse update(Long id, PregnancyCheckupRequest req) {
        PregnancyCheckup c = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("PregnancyCheckup not found: " + id));
        c.setDate(req.getDate()); c.setObservation(req.getObservation()); c.setWeightKg(req.getWeightKg()); c.setSymptoms(req.getSymptoms()); c.setFetalMovements(req.getFetalMovements());
        return toResponse(repo.save(c));
    }
    @Override @Transactional
    public void delete(Long id) { if (!repo.existsById(id)) throw new EntityNotFoundException("PregnancyCheckup not found: " + id); repo.deleteById(id); }
    private PregnancyCheckupResponse toResponse(PregnancyCheckup c) {
        return PregnancyCheckupResponse.builder().id(c.getId()).date(c.getDate()).observation(c.getObservation()).weightKg(c.getWeightKg()).symptoms(c.getSymptoms()).fetalMovements(c.getFetalMovements()).pregnancyTrackingId(c.getPregnancyTracking().getId()).build();
    }
}
