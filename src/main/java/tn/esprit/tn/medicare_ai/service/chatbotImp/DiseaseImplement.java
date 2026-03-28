package tn.esprit.tn.medicare_ai.service.chatbotImp;


import tn.esprit.tn.medicare_ai.dto.*;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiseaseRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.repository.*;
import tn.esprit.tn.medicare_ai.repository.chatbot.DiseaseRepository;
import tn.esprit.tn.medicare_ai.repository.chatbot.SpecialtyRepository;
import tn.esprit.tn.medicare_ai.repository.chatbot.SymptomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.DiseaseInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiseaseImplement implements DiseaseInterface {

    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;
    private final SpecialtyRepository specialtyRepository;

    // 🔹 Convert Entity → DTO
    private DiseaseResponseDTO mapToDTO(Disease d) {
        return DiseaseResponseDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .causes(d.getCauses())
                .treatment(d.getTreatment())
                .specialtyName(d.getSpecialty().getName())
                .symptoms(
                        d.getSymptoms()
                                .stream()
                                .map(Symptom::getName)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public DiseaseResponseDTO createDisease(DiseaseRequestDTO dto) {

        if (diseaseRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Disease already exists");
        }

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        List<Symptom> symptoms = symptomRepository.findAllById(dto.getSymptomIds());

        Disease disease = Disease.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .causes(dto.getCauses())
                .treatment(dto.getTreatment())
                .specialty(specialty)
                .symptoms(symptoms)
                .build();

        return mapToDTO(diseaseRepository.save(disease));
    }

    @Override
    public DiseaseResponseDTO updateDisease(Long id, DiseaseRequestDTO dto) {

        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        List<Symptom> symptoms = symptomRepository.findAllById(dto.getSymptomIds());

        disease.setName(dto.getName());
        disease.setDescription(dto.getDescription());
        disease.setCauses(dto.getCauses());
        disease.setTreatment(dto.getTreatment());
        disease.setSpecialty(specialty);
        disease.setSymptoms(symptoms);

        return mapToDTO(diseaseRepository.save(disease));
    }

    @Override
    public void deleteDisease(Long id) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        diseaseRepository.delete(disease);
    }

    @Override
    public DiseaseResponseDTO getDisease(Long id) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        return mapToDTO(disease);
    }

    @Override
    public List<DiseaseResponseDTO> getAllDiseases() {
        return diseaseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public DiseaseResponseDTO addSpecialty(Long diseaseId, Long specialtyId) {

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        disease.setSpecialty(specialty);

        return mapToDTO(diseaseRepository.save(disease));
    }
    @Override
    public DiseaseResponseDTO addSymptom(Long diseaseId, Long symptomId) {

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        Symptom symptom = symptomRepository.findById(symptomId)
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found"));

        if (!disease.getSymptoms().contains(symptom)) {
            disease.getSymptoms().add(symptom);
        }

        return mapToDTO(diseaseRepository.save(disease));
    }
    @Override
    public DiseaseResponseDTO removeSymptom(Long diseaseId, Long symptomId) {

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new EntityNotFoundException("Disease not found"));

        disease.getSymptoms().removeIf(s -> s.getId().equals(symptomId));

        return mapToDTO(diseaseRepository.save(disease));
    }
}
