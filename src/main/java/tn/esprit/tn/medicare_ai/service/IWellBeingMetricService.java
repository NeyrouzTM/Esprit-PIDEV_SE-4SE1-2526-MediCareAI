package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.WellBeingMetricRequest;
import tn.esprit.tn.medicare_ai.dto.response.WellBeingMetricResponse;
import java.util.List;
public interface IWellBeingMetricService {
    WellBeingMetricResponse create(WellBeingMetricRequest request);
    WellBeingMetricResponse getById(Long id);
    List<WellBeingMetricResponse> getAll();
    List<WellBeingMetricResponse> getByUserId(Long userId);
    WellBeingMetricResponse update(Long id, WellBeingMetricRequest request);
    void delete(Long id);
}
