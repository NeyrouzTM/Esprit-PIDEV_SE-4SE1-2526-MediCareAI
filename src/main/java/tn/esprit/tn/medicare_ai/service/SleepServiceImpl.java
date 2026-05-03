package tn.esprit.tn.medicare_ai.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.SleepRequest;
import tn.esprit.tn.medicare_ai.dto.response.SleepResponse;
import tn.esprit.tn.medicare_ai.entity.Sleep;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.SleepRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.ISleepService;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class SleepServiceImpl implements ISleepService {
    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;

    @Override @Transactional
    public SleepResponse create(SleepRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.getUserId()));
        return toResponse(sleepRepository.save(Sleep.builder()
                .hours(req.getHours()).quality(req.getQuality()).date(req.getDate()).user(user).build()));
    }
    @Override public SleepResponse getById(Long id) {
        return toResponse(sleepRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Sleep not found: " + id)));
    }
    @Override public List<SleepResponse> getAll() { return sleepRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override public List<SleepResponse> getByUserId(Long userId) { return sleepRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional
    public SleepResponse update(Long id, SleepRequest req) {
        Sleep s = sleepRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Sleep not found: " + id));
        s.setHours(req.getHours()); s.setQuality(req.getQuality()); s.setDate(req.getDate());
        return toResponse(sleepRepository.save(s));
    }
    @Override @Transactional
    public void delete(Long id) {
        if (!sleepRepository.existsById(id)) throw new EntityNotFoundException("Sleep not found: " + id);
        sleepRepository.deleteById(id);
    }
    private SleepResponse toResponse(Sleep s) {
        return SleepResponse.builder().id(s.getId()).hours(s.getHours()).quality(s.getQuality()).date(s.getDate()).userId(s.getUser().getId()).build();
    }
}
