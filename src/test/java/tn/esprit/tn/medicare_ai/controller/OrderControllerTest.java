package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tn.medicare_ai.dto.request.OrderItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PlaceOrderRequest;
import tn.esprit.tn.medicare_ai.dto.response.OrderDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.OrderResponse;
import tn.esprit.tn.medicare_ai.entity.OrderStatus;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.DrugInteractionException;
import tn.esprit.tn.medicare_ai.exception.InsufficientStockException;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private MedicineService medicineService;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private DrugInteractionService drugInteractionService;

    @MockitoBean
    private RefillService refillService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /api/pharmacy/orders: valid order returns 200")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void placeOrder_valid_returnsOk() throws Exception {
        mockCurrentPatient();
        when(orderService.placeOrder(any(PlaceOrderRequest.class), eq(1L)))
                .thenReturn(OrderDetailResponse.orderDetailBuilder()
                        .id(100L)
                        .status(OrderStatus.PENDING)
                        .totalAmount(50.0)
                        .orderDate(LocalDate.now())
                        .itemCount(1)
                        .build());

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/pharmacy/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/pharmacy/orders: insufficient stock returns 409")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void placeOrder_insufficientStock_returnsConflict() throws Exception {
        mockCurrentPatient();
        when(orderService.placeOrder(any(PlaceOrderRequest.class), eq(1L)))
                .thenThrow(new InsufficientStockException("Out of stock"));

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/pharmacy/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/pharmacy/orders: severe interaction returns 400")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void placeOrder_severeInteraction_returnsBadRequest() throws Exception {
        mockCurrentPatient();
        when(orderService.placeOrder(any(PlaceOrderRequest.class), eq(1L)))
                .thenThrow(new DrugInteractionException("Severe interaction"));

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(2).build()))
                .build();

        mockMvc.perform(post("/api/pharmacy/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pharmacy/orders: returns order history for patient")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void getOrderHistory_returnsOrders() throws Exception {
        mockCurrentPatient();
        when(orderService.getOrderHistory(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(OrderResponse.builder().id(1L).status(OrderStatus.PENDING).build())));

        mockMvc.perform(get("/api/pharmacy/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/pharmacy/orders/{id}: order exists returns 200")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void getOrderById_exists_returns200() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(OrderDetailResponse.orderDetailBuilder().id(1L).build());

        mockMvc.perform(get("/api/pharmacy/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/pharmacy/orders/{id}: not found returns 404")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void getOrderById_notFound_returns404() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/api/pharmacy/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/pharmacy/orders/{id}: unauthorized access returns 403")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void getOrderById_otherUser_returns403() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new UnauthorizedActionException("forbidden"));

        mockMvc.perform(get("/api/pharmacy/orders/99"))
                .andExpect(status().isForbidden());
    }

    private void mockCurrentPatient() {
        User patient = new User();
        patient.setId(1L);
        patient.setEmail("patient@med.com");
        patient.setRole(Role.PATIENT);
        when(userRepository.findByEmail("patient@med.com")).thenReturn(Optional.of(patient));
    }
}
