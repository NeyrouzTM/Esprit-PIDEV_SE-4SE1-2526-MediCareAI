package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.RecommendationRequest;
import tn.esprit.tn.medicare_ai.dto.response.RecommendationResponse;
import tn.esprit.tn.medicare_ai.entity.RecommendationCategory;
import java.util.List;
public interface IRecommendationService {
    RecommendationResponse create(RecommendationRequest request);
    RecommendationResponse getById(Long id);
    List<RecommendationResponse> getAll();
    List<RecommendationResponse> getByPregnancyTrackingId(Long pregnancyTrackingId);
    List<RecommendationResponse> getByPregnancyTrackingIdAndCategory(Long pregnancyTrackingId, RecommendationCategory category);
    RecommendationResponse update(Long id, RecommendationRequest request);
    void delete(Long id);
}
