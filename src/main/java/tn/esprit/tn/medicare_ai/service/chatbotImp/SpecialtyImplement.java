package tn.esprit.tn.medicare_ai.service.chatbotImp;


import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.SpecialtyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.repository.chatbot.SpecialtyRepository;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.SpecialtyInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyImplement implements SpecialtyInterface {

    private final SpecialtyRepository specialtyRepository;

    // 🔹 Entity → DTO
    private SpecialtyResponseDTO mapToDTO(Specialty s) {
        return SpecialtyResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .diseases(
                        s.getDiseases() != null ?
                                s.getDiseases()
                                        .stream()
                                        .map(Disease::getName)
                                        .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    // 🔹 DTO → Entity
    private Specialty mapToEntity(SpecialtyRequestDTO dto) {
        return Specialty.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    @Override
    public SpecialtyResponseDTO createSpecialty(SpecialtyRequestDTO dto) {

        if (specialtyRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Specialty already exists");
        }

        Specialty specialty = mapToEntity(dto);
        return mapToDTO(specialtyRepository.save(specialty));
    }

    @Override
    public SpecialtyResponseDTO updateSpecialty(Long id, SpecialtyRequestDTO dto) {

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        if (!specialty.getName().equals(dto.getName()) &&
                specialtyRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Specialty already exists");
        }

        specialty.setName(dto.getName());
        specialty.setDescription(dto.getDescription());

        return mapToDTO(specialtyRepository.save(specialty));
    }

    @Override
    public void deleteSpecialty(Long id) {

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        specialtyRepository.delete(specialty);
    }

    @Override
    public SpecialtyResponseDTO getSpecialty(Long id) {

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        return mapToDTO(specialty);
    }

    @Override
    public List<SpecialtyResponseDTO> getAllSpecialties() {

        return specialtyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
