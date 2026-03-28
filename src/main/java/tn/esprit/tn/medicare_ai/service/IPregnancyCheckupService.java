package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.PregnancyCheckupRequest;
import tn.esprit.tn.medicare_ai.dto.response.PregnancyCheckupResponse;
import java.util.List;
public interface IPregnancyCheckupService {
    PregnancyCheckupResponse create(PregnancyCheckupRequest request);
    PregnancyCheckupResponse getById(Long id);
    List<PregnancyCheckupResponse> getAll();
    List<PregnancyCheckupResponse> getByPregnancyTrackingId(Long pregnancyTrackingId);
    PregnancyCheckupResponse update(Long id, PregnancyCheckupRequest request);
    void delete(Long id);
}
