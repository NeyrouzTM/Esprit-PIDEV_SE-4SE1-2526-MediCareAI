package tn.esprit.tn.medicare_ai.service.chatbotinterface;

import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SymptomRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SymptomResponseDTO;

import java.util.List;

public interface SymptomInterface {

    SymptomResponseDTO createSymptom(SymptomRequestDTO dto);

    SymptomResponseDTO updateSymptom(Long id, SymptomRequestDTO dto);

    void deleteSymptom(Long id);

    SymptomResponseDTO getSymptom(Long id);

    List<SymptomResponseDTO> getAllSymptoms();
}