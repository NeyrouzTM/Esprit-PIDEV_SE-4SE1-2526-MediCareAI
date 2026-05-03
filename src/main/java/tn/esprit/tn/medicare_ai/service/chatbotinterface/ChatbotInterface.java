package tn.esprit.tn.medicare_ai.service.chatbotinterface;

import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiagnosisRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;

import java.util.List;

public interface ChatbotInterface {
    List<DiseaseResponseDTO> diagnoseForDoctor(DiagnosisRequestDTO request);
    List<SpecialtyResponseDTO> diagnoseForPatient(DiagnosisRequestDTO request);
}
