package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.StressRequest;
import tn.esprit.tn.medicare_ai.dto.response.StressResponse;
import tn.esprit.tn.medicare_ai.entity.Stress;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.StressRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IStressService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class StressServiceImpl implements IStressService {
    private final StressRepository stressRepository;
    private final UserRepository userRepository;

    @Override @Transactional
    public StressResponse create(StressRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        return toResponse(stressRepository.save(Stress.builder().level(req.getLevel()).message(req.getMessage()).date(req.getDate()).user(user).build()));
    }
    @Override public StressResponse getById(Long id) { return toResponse(stressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stress not found: " + id))); }
    @Override public List<StressResponse> getAll() { return stressRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<StressResponse> getByUserId(Long userId) { return stressRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public StressResponse update(Long id, StressRequest req) {
        Stress s = stressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stress not found: " + id));
        s.setLevel(req.getLevel()); s.setMessage(req.getMessage()); s.setDate(req.getDate());
        return toResponse(stressRepository.save(s));
    }
    @Override @Transactional
    public void delete(Long id) {
        if (!stressRepository.existsById(id)) throw new EntityNotFoundException("Stress not found: " + id);
        stressRepository.deleteById(id);
    }
    private StressResponse toResponse(Stress s) {
        return StressResponse.builder().id(s.getId()).level(s.getLevel()).message(s.getMessage()).date(s.getDate()).userId(s.getUser().getId()).build();
    }
}
