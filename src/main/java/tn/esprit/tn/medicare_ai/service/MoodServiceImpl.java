package tn.esprit.tn.medicare_ai.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.MoodRequest;
import tn.esprit.tn.medicare_ai.dto.response.MoodResponse;
import tn.esprit.tn.medicare_ai.entity.Mood;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.MoodRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.IMoodService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodServiceImpl implements IMoodService {

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MoodResponse create(MoodRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));
        Mood mood = Mood.builder()
                .level(request.getLevel())
                .note(request.getNote())
                .date(request.getDate())
                .user(user)
                .build();
        return toResponse(moodRepository.save(mood));
    }

    @Override
    public MoodResponse getById(Long id) {
        return toResponse(moodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mood not found with id: " + id)));
    }

    @Override
    public List<MoodResponse> getAll() {
        return moodRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<MoodResponse> getByUserId(Long userId) {
        return moodRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MoodResponse update(Long id, MoodRequest request) {
        Mood mood = moodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mood not found with id: " + id));
        mood.setLevel(request.getLevel());
        mood.setNote(request.getNote());
        mood.setDate(request.getDate());
        return toResponse(moodRepository.save(mood));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!moodRepository.existsById(id))
            throw new EntityNotFoundException("Mood not found with id: " + id);
        moodRepository.deleteById(id);
    }

    private MoodResponse toResponse(Mood m) {
        return MoodResponse.builder()
                .id(m.getId()).level(m.getLevel()).note(m.getNote())
                .date(m.getDate()).userId(m.getUser().getId())
                .build();
    }
}
