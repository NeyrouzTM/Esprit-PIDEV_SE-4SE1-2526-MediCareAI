package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.StressRequest;
import tn.esprit.tn.medicare_ai.dto.response.StressResponse;
import java.util.List;
public interface IStressService {
    StressResponse create(StressRequest request);
    StressResponse getById(Long id);
    List<StressResponse> getAll();
    List<StressResponse> getByUserId(Long userId);
    StressResponse update(Long id, StressRequest request);
    void delete(Long id);
}
