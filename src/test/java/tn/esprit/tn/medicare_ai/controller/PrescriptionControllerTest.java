package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionControllerTest {

    @Mock
    private PrescriptionService prescriptionService;

    @Mock
    private MedicineService medicineService;

    @Mock
    private OrderService orderService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private DrugInteractionService drugInteractionService;

    @Mock
    private RefillService refillService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PharmacyController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /api/pharmacy/prescriptions: doctor creates prescription successfully")
    void createPrescription_validDoctor_returnsCreated() {
        mockCurrentDoctor();
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest("POST", "/api/pharmacy/prescriptions"))
        );
        when(prescriptionService.createPrescription(any(PrescriptionRequest.class), org.mockito.ArgumentMatchers.eq(20L)))
                .thenReturn(PrescriptionDetailResponse.prescriptionDetailBuilder().id(100L).build());

        ResponseEntity<PrescriptionDetailResponse> response = controller.createPrescription(new PrescriptionRequest());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getId());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getPrescriptionById_returnsOk() {
        when(prescriptionService.getPrescriptionById(100L)).thenReturn(PrescriptionDetailResponse.prescriptionDetailBuilder().id(100L).build());

        ResponseEntity<PrescriptionDetailResponse> response = controller.getPrescriptionById(100L);

        assertEquals(200, response.getStatusCode().value());
    }

    private void mockCurrentDoctor() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("doctor@med.com", null));
        User doctor = new User();
        doctor.setId(20L);
        doctor.setEmail("doctor@med.com");
        doctor.setRole(Role.DOCTOR);
        when(userRepository.findByEmail("doctor@med.com")).thenReturn(Optional.of(doctor));
    }
}
