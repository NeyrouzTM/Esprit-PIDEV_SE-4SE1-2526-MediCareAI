package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.DrugInteractionCheckRequest;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionCheckResponse;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionControllerTest {

    @Mock private MedicineService medicineService;
    @Mock private PrescriptionService prescriptionService;
    @Mock private OrderService orderService;
    @Mock private InventoryService inventoryService;
    @Mock private DrugInteractionService drugInteractionService;
    @Mock private RefillService refillService;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PharmacyController controller;

    @Test
    void checkInteractions_returnsOk() {
        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(1L, 2L))
                .build();
        DrugInteractionCheckResponse expected = DrugInteractionCheckResponse.builder()
                .hasSevereInteraction(true)
                .build();

        when(drugInteractionService.checkInteractions(request)).thenReturn(expected);

        ResponseEntity<DrugInteractionCheckResponse> response = controller.checkInteractions(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody().isHasSevereInteraction());
    }
}
