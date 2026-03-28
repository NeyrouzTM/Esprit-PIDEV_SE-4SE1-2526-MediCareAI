package tn.esprit.tn.medicare_ai.service;
import tn.esprit.tn.medicare_ai.dto.request.MedicationReminderRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicationReminderResponse;
import java.util.List;
public interface IMedicationReminderService {
    MedicationReminderResponse create(MedicationReminderRequest request);
    MedicationReminderResponse getById(Long id);
    List<MedicationReminderResponse> getAll();
    List<MedicationReminderResponse> getByScheduleId(Long scheduleId);
    MedicationReminderResponse update(Long id, MedicationReminderRequest request);
    void delete(Long id);
}
