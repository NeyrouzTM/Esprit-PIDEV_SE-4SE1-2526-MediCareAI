package tn.esprit.tn.medicare_ai.service.chatbotinterface;


import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SpecialtyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;

import java.util.List;

public interface SpecialtyInterface {

    SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO dto);

    SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO dto);

    void deleteSpecialty(Long id);

    SpecialtyResponseDTO getSpecialty(Long id);

    List<SpecialtyResponseDTO> getAllSpecialties();
}