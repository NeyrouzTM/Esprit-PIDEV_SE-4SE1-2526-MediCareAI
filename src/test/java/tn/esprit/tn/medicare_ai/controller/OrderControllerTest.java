package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.tn.medicare_ai.dto.request.PlaceOrderRequest;
import tn.esprit.tn.medicare_ai.dto.response.OrderDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.OrderResponse;
import tn.esprit.tn.medicare_ai.entity.OrderStatus;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock private MedicineService medicineService;
    @Mock private PrescriptionService prescriptionService;
    @Mock private OrderService orderService;
    @Mock private InventoryService inventoryService;
    @Mock private DrugInteractionService drugInteractionService;
    @Mock private RefillService refillService;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PharmacyController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void placeOrder_returnsOk() {
        mockCurrentPatient();
        when(orderService.placeOrder(any(PlaceOrderRequest.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(OrderDetailResponse.orderDetailBuilder().id(10L).status(OrderStatus.PENDING).build());

        ResponseEntity<OrderDetailResponse> response = controller.placeOrder(new PlaceOrderRequest());

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void getOrderHistory_returnsPage() {
        mockCurrentPatient();
        Page<OrderResponse> page = new PageImpl<>(java.util.List.of(OrderResponse.builder().id(1L).status(OrderStatus.PENDING).build()));
        when(orderService.getOrderHistory(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(page);

        ResponseEntity<Page<OrderResponse>> response = controller.getOrderHistory(org.springframework.data.domain.Pageable.unpaged());

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getOrderById_returnsOk() {
        when(orderService.getOrderById(5L)).thenReturn(OrderDetailResponse.orderDetailBuilder().id(5L).build());

        ResponseEntity<OrderDetailResponse> response = controller.getOrderById(5L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void cancelAndDeleteOrder_delegateToService() {
        mockCurrentPatient();

        controller.cancelOrder(5L);
        ResponseEntity<Void> deleteResponse = controller.deleteOrder(5L);

        verify(orderService).cancelOrder(5L);
        verify(orderService).deleteOrder(5L, 1L);
        assertEquals(204, deleteResponse.getStatusCode().value());
    }

    private void mockCurrentPatient() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("patient@med.com", null));
        User patient = new User();
        patient.setId(1L);
        patient.setEmail("patient@med.com");
        patient.setRole(Role.PATIENT);
        when(userRepository.findByEmail("patient@med.com")).thenReturn(Optional.of(patient));
    }
}
