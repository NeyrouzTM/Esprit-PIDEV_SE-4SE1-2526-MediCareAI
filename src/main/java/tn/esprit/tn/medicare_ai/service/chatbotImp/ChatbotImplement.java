package tn.esprit.tn.medicare_ai.service.chatbotImp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.ChatbotRequestDTO.DiagnosisRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.DiseaseResponseDTO;
import tn.esprit.tn.medicare_ai.dto.response.chatbotResponseDTO.SpecialtyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Disease;
import tn.esprit.tn.medicare_ai.entity.Specialty;
import tn.esprit.tn.medicare_ai.entity.Symptom;
import tn.esprit.tn.medicare_ai.repository.chatbot.DiseaseRepository;
import tn.esprit.tn.medicare_ai.service.chatbotinterface.ChatbotInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotImplement implements ChatbotInterface {

    private final DiseaseRepository diseaseRepository;

    @Override
    public List<DiseaseResponseDTO> diagnoseForDoctor(DiagnosisRequestDTO request) {
        List<Disease> matchedDiseases = getMatchingDiseases(request);
        return matchedDiseases.stream()
                .map(this::mapToDiseaseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialtyResponseDTO> diagnoseForPatient(DiagnosisRequestDTO request) {
        List<Disease> matchedDiseases = getMatchingDiseases(request);

        Set<Specialty> uniqueSpecialties = new HashSet<>();
        for (Disease d : matchedDiseases) {
            if (d.getSpecialty() != null) {
                uniqueSpecialties.add(d.getSpecialty());
            }
        }

        return uniqueSpecialties.stream()
                .map(this::mapToSpecialtyDTO)
                .collect(Collectors.toList());
    }

    private List<Disease> getMatchingDiseases(DiagnosisRequestDTO request) {
        List<Disease> allDiseases = diseaseRepository.findAll();

        boolean hasNames = request.getSymptomNames() != null && !request.getSymptomNames().isEmpty();

        if (!hasNames) {
            return new ArrayList<>();
        }

        return allDiseases.stream()
                .filter(disease -> {
                    if (disease.getSymptoms() == null) return false;

                    List<String> diseaseSymptomNames = disease.getSymptoms().stream()
                            .map(s -> s.getName().toLowerCase())
                            .collect(Collectors.toList());

                    List<String> requestNamesLower = request.getSymptomNames().stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());

                    return diseaseSymptomNames.containsAll(requestNamesLower);
                })
                .collect(Collectors.toList());
    }

    private DiseaseResponseDTO mapToDiseaseDTO(Disease d) {
        return DiseaseResponseDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .causes(d.getCauses())
                .treatment(d.getTreatment())
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : null)
                .symptoms(
                        d.getSymptoms() != null ?
                        d.getSymptoms().stream()
                                .map(Symptom::getName)
                                .collect(Collectors.toList()) : new ArrayList<>()
                )
                .build();
    }

    private SpecialtyResponseDTO mapToSpecialtyDTO(Specialty s) {
        return SpecialtyResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .diseases(
                        s.getDiseases() != null ?
                                s.getDiseases().stream()
                                        .map(Disease::getName)
                                        .collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .build();
    }
}
