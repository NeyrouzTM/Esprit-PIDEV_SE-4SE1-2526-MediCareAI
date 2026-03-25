package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.DrugInteractionCheckRequest;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionCheckResponse;
import tn.esprit.tn.medicare_ai.entity.DrugInteraction;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.repository.DrugInteractionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DrugInteractionServiceTest {

    @Mock
    private DrugInteractionRepository drugInteractionRepository;

    @InjectMocks
    private DrugInteractionService drugInteractionService;

    @Test
    @DisplayName("checkInteractions: known interaction returns alerts")
    void checkInteractions_knownInteraction_returnsAlerts() {
        DrugInteraction interaction = new DrugInteraction();
        interaction.setMedicineA(medicine(1L, "Aspirin"));
        interaction.setMedicineB(medicine(2L, "Warfarin"));
        interaction.setSeverity("SEVERE");
        interaction.setDescription("Bleeding risk");
        interaction.setRecommendation("Avoid combination");

        when(drugInteractionRepository.findInteractionsForMedicines(List.of(1L, 2L)))
                .thenReturn(List.of(interaction));

        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(1L, 2L))
                .build();

        DrugInteractionCheckResponse response = drugInteractionService.checkInteractions(request);

        assertEquals(1, response.getAlerts().size());
        assertTrue(response.isHasSevereInteraction());
    }

    @Test
    @DisplayName("checkInteractions: no interaction returns empty alerts")
    void checkInteractions_noInteraction_returnsEmpty() {
        when(drugInteractionRepository.findInteractionsForMedicines(anyList())).thenReturn(List.of());

        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(5L, 6L))
                .build();

        DrugInteractionCheckResponse response = drugInteractionService.checkInteractions(request);

        assertTrue(response.getAlerts().isEmpty());
        assertFalse(response.isHasSevereInteraction());
    }

    @Test
    @DisplayName("checkInteractions: request with patientId still checks medicine IDs")
    void checkInteractions_withPatientId_checksMedicineIds() {
        when(drugInteractionRepository.findInteractionsForMedicines(List.of(7L, 8L))).thenReturn(List.of());

        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(7L, 8L))
                .patientId(100L)
                .build();

        drugInteractionService.checkInteractions(request);

        verify(drugInteractionRepository).findInteractionsForMedicines(List.of(7L, 8L));
    }

    private Medicine medicine(Long id, String name) {
        Medicine medicine = new Medicine();
        medicine.setId(id);
        medicine.setName(name);
        return medicine;
    }
}

