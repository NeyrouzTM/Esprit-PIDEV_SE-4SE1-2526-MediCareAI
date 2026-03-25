package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.OrderItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PlaceOrderRequest;
import tn.esprit.tn.medicare_ai.dto.response.OrderDetailResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.exception.DrugInteractionException;
import tn.esprit.tn.medicare_ai.exception.InsufficientStockException;
import tn.esprit.tn.medicare_ai.exception.InvalidPrescriptionException;
import tn.esprit.tn.medicare_ai.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private MedicineRepository medicineRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private PrescriptionItemRepository prescriptionItemRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private DrugInteractionService drugInteractionService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("placeOrder: sufficient stock places order, decreases stock, sets pending, calculates total")
    void placeOrder_sufficientStock_success() {
        User patient = user(1L, Role.PATIENT);
        Medicine med = medicine(10L, "Amoxicillin", 12.5, false);
        Inventory inventory = inventory(10L, med, 20);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(med));
        when(inventoryRepository.findByMedicineId(10L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(2).build()))
                .build();

        OrderDetailResponse response = orderService.placeOrder(request, 1L);

        assertEquals(100L, response.getId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(25.0, response.getTotalAmount());
        assertEquals(1, response.getItemCount());
        assertEquals(18, inventory.getStockQuantity());
        verify(inventoryRepository, atLeastOnce()).save(inventory);
    }

    @Test
    @DisplayName("placeOrder: insufficient stock throws InsufficientStockException")
    void placeOrder_insufficientStock_throws() {
        User patient = user(1L, Role.PATIENT);
        Medicine med = medicine(10L, "Amoxicillin", 12.5, false);
        Inventory inventory = inventory(10L, med, 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(med));
        when(inventoryRepository.findByMedicineId(10L)).thenReturn(Optional.of(inventory));

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(2).build()))
                .build();

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(request, 1L));
    }

    @Test
    @DisplayName("placeOrder: prescription-required medicine without prescription throws InvalidPrescriptionException")
    void placeOrder_prescriptionRequiredWithoutPrescription_throws() {
        User patient = user(1L, Role.PATIENT);
        Medicine med = medicine(10L, "ControlledMed", 50.0, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(med));

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(1).build()))
                .build();

        assertThrows(InvalidPrescriptionException.class, () -> orderService.placeOrder(request, 1L));
    }

    @Test
    @DisplayName("placeOrder: severe interaction throws DrugInteractionException")
    void placeOrder_severeInteraction_throws() {
        User patient = user(1L, Role.PATIENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        doThrow(new DrugInteractionException("Severe interaction"))
                .when(drugInteractionService).assertNoSevereInteraction(anyList());

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .shippingAddress("Main Street")
                .items(List.of(OrderItemRequest.builder().medicineId(10L).quantity(1).build()))
                .build();

        assertThrows(DrugInteractionException.class, () -> orderService.placeOrder(request, 1L));
    }

    private User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private Medicine medicine(Long id, String name, double price, boolean requiresPrescription) {
        Medicine medicine = new Medicine();
        medicine.setId(id);
        medicine.setName(name);
        medicine.setPrice(price);
        medicine.setPrescriptionRequired(requiresPrescription);
        return medicine;
    }

    private Inventory inventory(Long id, Medicine medicine, Integer stock) {
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setMedicine(medicine);
        inventory.setStockQuantity(stock);
        inventory.setWarehouseLocation("MAIN");
        return inventory;
    }
}

