package tn.esprit.tn.medicare_ai.service.interfaces;



import tn.esprit.tn.medicare_ai.dto.request.MeetingRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import java.util.List;

public interface MeetingService {

    MeetingResponseDTO createMeeting(MeetingRequestDTO dto, Long organizerId);
    List<MeetingResponseDTO> getAllMeetings();
    MeetingResponseDTO getMeetingById(Long id);
    MeetingResponseDTO updateMeeting(Long id, MeetingRequestDTO dto, Long organizerId);
    void deleteMeeting(Long id, Long organizerId);
}
