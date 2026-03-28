package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyTrackingRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyTrackingResponse;
import java.util.List;
public interface IPregnancyTrackingService {
    PregnancyTrackingResponse create(PregnancyTrackingRequest request);
    PregnancyTrackingResponse getById(Long id);
    List<PregnancyTrackingResponse> getAll();
    List<PregnancyTrackingResponse> getByUserId(Long userId);
    PregnancyTrackingResponse update(Long id, PregnancyTrackingRequest request);
    void delete(Long id);
}
