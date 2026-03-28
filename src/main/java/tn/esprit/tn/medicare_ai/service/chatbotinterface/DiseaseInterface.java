package tn.esprit.tn.medicare_ai.service.chatbotinterface;

import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;

import java.util.List;

public interface DiseaseInterface {

    DiseaseResponseDTO createDisease(DiseaseRequestDTO dto);

    DiseaseResponseDTO updateDisease(Long id, DiseaseRequestDTO dto);

    void deleteDisease(Long id);

    DiseaseResponseDTO getDisease(Long id);

    List<DiseaseResponseDTO> getAllDiseases();

    DiseaseResponseDTO addSpecialty(Long diseaseId, Long specialtyId);

    DiseaseResponseDTO addSymptom(Long diseaseId, Long symptomId);

    DiseaseResponseDTO removeSymptom(Long diseaseId, Long symptomId);

}