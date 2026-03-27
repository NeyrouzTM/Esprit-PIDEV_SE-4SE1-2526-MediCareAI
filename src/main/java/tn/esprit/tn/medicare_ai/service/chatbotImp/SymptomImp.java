package tn.esprit.tn.medicare_ai.service.chatbotImp;

import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SymptomRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SymptomResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Symptom;
import tn.esprit.tn.medicare_ai.repository.chatbot.SymptomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SymptomInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SymptomImp implements SymptomInterface {
    private final SymptomRepository symptomRepository;

    // 🔹 Convert Entity → ResponseDTO
    private SymptomResponseDTO mapToDTO(Symptom s) {
        return SymptomResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }

    // 🔹 Convert RequestDTO → Entity
    private Symptom mapToEntity(SymptomRequestDTO dto) {
        return Symptom.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    @Override
    public SymptomResponseDTO createSymptom(SymptomRequestDTO dto) {
        if (symptomRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Symptom already exists");
        }

        Symptom symptom = mapToEntity(dto);
        return mapToDTO(symptomRepository.save(symptom));
    }

    @Override
    public SymptomResponseDTO updateSymptom(Long id, SymptomRequestDTO dto) {
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found"));

        if (!symptom.getName().equals(dto.getName()) &&
                symptomRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Symptom already exists");
        }

        symptom.setName(dto.getName());
        symptom.setDescription(dto.getDescription());

        return mapToDTO(symptomRepository.save(symptom));
    }

    @Override
    public void deleteSymptom(Long id) {
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found"));

        symptomRepository.delete(symptom);
    }

    @Override
    public SymptomResponseDTO getSymptom(Long id) {
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found"));

        return mapToDTO(symptom);
    }

    @Override
    public List<SymptomResponseDTO> getAllSymptoms() {
        return symptomRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
