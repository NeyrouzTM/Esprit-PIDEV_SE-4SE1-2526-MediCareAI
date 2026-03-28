package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.MedicationScheduleRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationScheduleResponse;
import java.util.List;
public interface IMedicationScheduleService {
    MedicationScheduleResponse create(MedicationScheduleRequest request);
    MedicationScheduleResponse getById(Long id);
    List<MedicationScheduleResponse> getAll();
    List<MedicationScheduleResponse> getByUserId(Long userId);
    MedicationScheduleResponse update(Long id, MedicationScheduleRequest request);
    void delete(Long id);
}
