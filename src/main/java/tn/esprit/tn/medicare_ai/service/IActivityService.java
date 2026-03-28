package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.ActivityRequest;
import tn.esprit.tn.medicare_ai.dto.response.ActivityResponse;
import java.util.List;
public interface IActivityService {
    ActivityResponse create(ActivityRequest request);
    ActivityResponse getById(Long id);
    List<ActivityResponse> getAll();
    List<ActivityResponse> getByPregnancyTrackingId(Long pregnancyTrackingId);
    ActivityResponse update(Long id, ActivityRequest request);
    void delete(Long id);
}
