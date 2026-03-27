package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.MedicineSearchRequest;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionAlertDto;
import tn.esprit.tn.medicare_ai.dto.response.MedicineDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.entity.DrugInteraction;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.DrugInteractionRepository;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final DrugInteractionRepository drugInteractionRepository;

    public Page<MedicineResponse> searchMedicines(MedicineSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        return medicineRepository.search(
                request.getKeyword(),
                request.getCategory(),
                request.getPrescriptionRequired(),
                pageable
        ).map(this::toMedicineResponse);
    }

    public MedicineDetailResponse getMedicineDetail(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));

        List<DrugInteractionAlertDto> alerts = drugInteractionRepository
                .findByMedicineAIdOrMedicineBId(id, id)
                .stream()
                .map(this::toAlert)
                .toList();

        return MedicineDetailResponse.medicineDetailBuilder()
                .id(medicine.getId())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .dosageForm(medicine.getDosageForm())
                .strength(medicine.getStrength())
                .price(medicine.getPrice())
                .prescriptionRequired(medicine.getPrescriptionRequired())
                .imageUrl(medicine.getImageUrl())
                .manufacturer(medicine.getManufacturer())
                .description(medicine.getDescription())
                .interactionAlerts(alerts)
                .build();
    }

    private MedicineResponse toMedicineResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .dosageForm(medicine.getDosageForm())
                .strength(medicine.getStrength())
                .price(medicine.getPrice())
                .prescriptionRequired(medicine.getPrescriptionRequired())
                .imageUrl(medicine.getImageUrl())
                .build();
    }

    private DrugInteractionAlertDto toAlert(DrugInteraction interaction) {
        return DrugInteractionAlertDto.builder()
                .medicineAName(interaction.getMedicineA() != null ? interaction.getMedicineA().getName() : null)
                .medicineBName(interaction.getMedicineB() != null ? interaction.getMedicineB().getName() : null)
                .severity(interaction.getSeverity())
                .description(interaction.getDescription())
                .recommendation(interaction.getRecommendation())
                .build();
    }
}

