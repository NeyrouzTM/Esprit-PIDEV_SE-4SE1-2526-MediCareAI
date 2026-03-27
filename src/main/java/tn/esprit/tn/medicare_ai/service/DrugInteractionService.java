package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.DrugInteractionCheckRequest;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionAlertDto;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionCheckResponse;
import tn.esprit.tn.medicare_ai.entity.DrugInteraction;
import tn.esprit.tn.medicare_ai.exception.DrugInteractionException;
import tn.esprit.tn.medicare_ai.repository.DrugInteractionRepository;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DrugInteractionService {

    private final DrugInteractionRepository drugInteractionRepository;

    public DrugInteractionCheckResponse checkInteractions(DrugInteractionCheckRequest request) {
        List<DrugInteraction> interactions = drugInteractionRepository.findInteractionsForMedicines(request.getMedicineIds());
        List<DrugInteractionAlertDto> alerts = interactions.stream().map(this::toAlert).toList();

        boolean hasSevere = alerts.stream()
                .anyMatch(a -> "SEVERE".equalsIgnoreCase(a.getSeverity()));

        return DrugInteractionCheckResponse.builder()
                .alerts(alerts)
                .hasSevereInteraction(hasSevere)
                .build();
    }

    public void assertNoSevereInteraction(List<Long> medicineIds) {
        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(medicineIds)
                .build();
        DrugInteractionCheckResponse response = checkInteractions(request);

        if (response.isHasSevereInteraction()) {
            throw new DrugInteractionException("Severe drug interaction detected for selected medicines");
        }
    }

    private DrugInteractionAlertDto toAlert(DrugInteraction interaction) {
        return DrugInteractionAlertDto.builder()
                .medicineAName(interaction.getMedicineA() != null ? interaction.getMedicineA().getName() : null)
                .medicineBName(interaction.getMedicineB() != null ? interaction.getMedicineB().getName() : null)
                .severity(interaction.getSeverity() != null ? interaction.getSeverity().toUpperCase(Locale.ROOT) : null)
                .description(interaction.getDescription())
                .recommendation(interaction.getRecommendation())
                .build();
    }
}

