package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.SleepRequest;
import tn.esprit.tn.medicare_ai.dto.response.SleepResponse;
import java.util.List;
public interface ISleepService {
    SleepResponse create(SleepRequest request);
    SleepResponse getById(Long id);
    List<SleepResponse> getAll();
    List<SleepResponse> getByUserId(Long userId);
    SleepResponse update(Long id, SleepRequest request);
    void delete(Long id);
}
