package tn.esprit.tn.medicare_ai.service;

import tn.esprit.tn.medicare_ai.dto.request.MoodRequest;
import tn.esprit.tn.medicare_ai.dto.response.MoodResponse;
import java.util.List;

public interface IMoodService {
    MoodResponse create(MoodRequest request);
    MoodResponse getById(Long id);
    List<MoodResponse> getAll();
    List<MoodResponse> getByUserId(Long userId);
    MoodResponse update(Long id, MoodRequest request);
    void delete(Long id);
}
