package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tn.esprit.tn.medicare_ai.dto.request.CreateMedicineRequest;
import tn.esprit.tn.medicare_ai.dto.request.MedicineSearchRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicineDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicineControllerTest {

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
    void searchMedicines_returnsPage() {
        Page<MedicineResponse> page = new PageImpl<>(List.of(MedicineResponse.builder().id(1L).name("Paracetamol").build()));
        when(medicineService.searchMedicines(any(MedicineSearchRequest.class))).thenReturn(page);

        ResponseEntity<Page<MedicineResponse>> response = controller.searchMedicines(new MedicineSearchRequest());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void createMedicine_returnsCreated() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest("POST", "/api/pharmacy/medicines"))
        );
        MedicineDetailResponse created = MedicineDetailResponse.medicineDetailBuilder().id(99L).name("Ibuprofen").build();
        when(medicineService.createMedicine(any(CreateMedicineRequest.class))).thenReturn(created);

        ResponseEntity<MedicineDetailResponse> response = controller.createMedicine(new CreateMedicineRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(99L, response.getBody().getId());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void updateMedicine_returnsOk() {
        when(medicineService.updateMedicine(org.mockito.ArgumentMatchers.eq(10L), any(CreateMedicineRequest.class)))
                .thenReturn(MedicineDetailResponse.medicineDetailBuilder().id(10L).build());

        ResponseEntity<MedicineDetailResponse> response = controller.updateMedicine(10L, new CreateMedicineRequest());

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteMedicine_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteMedicine(10L);

        verify(medicineService).deleteMedicine(10L);
        assertEquals(204, response.getStatusCode().value());
    }
}
