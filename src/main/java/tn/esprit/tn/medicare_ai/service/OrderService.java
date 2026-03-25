package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.OrderItemRequest;
import tn.esprit.tn.medicare_ai.dto.request.PlaceOrderRequest;
import tn.esprit.tn.medicare_ai.dto.response.OrderDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.OrderItemResponse;
import tn.esprit.tn.medicare_ai.dto.response.OrderResponse;
import tn.esprit.tn.medicare_ai.entity.*;
import tn.esprit.tn.medicare_ai.exception.*;
import tn.esprit.tn.medicare_ai.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final DrugInteractionService drugInteractionService;

    public OrderDetailResponse placeOrder(PlaceOrderRequest request, Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        Prescription prescription = null;
        if (request.getPrescriptionId() != null) {
            prescription = prescriptionRepository.findById(request.getPrescriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + request.getPrescriptionId()));

            if (prescription.getPatient() == null || !prescription.getPatient().getId().equals(patientId)) {
                throw new UnauthorizedActionException("Prescription does not belong to current patient");
            }
            if (prescription.getExpiryDate() != null && prescription.getExpiryDate().isBefore(LocalDate.now())) {
                throw new PrescriptionExpiredException("Prescription is expired");
            }
        }

        List<Long> medicineIds = request.getItems().stream().map(OrderItemRequest::getMedicineId).toList();
        drugInteractionService.assertNoSevereInteraction(medicineIds);

        Order order = new Order();
        order.setPatient(patient);
        order.setPrescription(prescription);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0d;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Medicine medicine = medicineRepository.findById(itemRequest.getMedicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + itemRequest.getMedicineId()));

            if (Boolean.TRUE.equals(medicine.getPrescriptionRequired()) && prescription == null) {
                throw new InvalidPrescriptionException("Prescription is required for medicine: " + medicine.getName());
            }

            Inventory inventory = inventoryRepository.findByMedicineId(medicine.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for medicine: " + medicine.getId()));

            if (inventory.getStockQuantity() == null || inventory.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for medicine: " + medicine.getName());
            }

            PrescriptionItem prescriptionItem = null;
            if (itemRequest.getPrescriptionItemId() != null) {
                prescriptionItem = prescriptionItemRepository.findById(itemRequest.getPrescriptionItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Prescription item not found: " + itemRequest.getPrescriptionItemId()));
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMedicine(medicine);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(medicine.getPrice());
            orderItem.setPrescriptionItem(prescriptionItem);
            orderItems.add(orderItem);

            inventory.setStockQuantity(inventory.getStockQuantity() - itemRequest.getQuantity());
            inventoryRepository.save(inventory);

            total += (medicine.getPrice() != null ? medicine.getPrice() : 0d) * itemRequest.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return toOrderDetail(saved);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return toOrderDetail(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderHistory(Long patientId, Pageable pageable) {
        return orderRepository.findByPatientIdOrderByOrderDateDesc(patientId, pageable)
                .map(this::toOrderSummary);
    }

    public OrderDetailResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (!(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PROCESSING)) {
            throw new UnauthorizedActionException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getMedicine() != null) {
                    inventoryRepository.findByMedicineId(item.getMedicine().getId()).ifPresent(inv -> {
                        int current = inv.getStockQuantity() != null ? inv.getStockQuantity() : 0;
                        inv.setStockQuantity(current + (item.getQuantity() != null ? item.getQuantity() : 0));
                        inventoryRepository.save(inv);
                    });
                }
            }
        }

        return toOrderDetail(orderRepository.save(order));
    }

    public void deleteOrder(Long id, Long patientId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (order.getPatient() == null || !order.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedActionException("Order does not belong to current patient");
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new UnauthorizedActionException("Only cancelled orders can be deleted");
        }

        orderRepository.delete(order);
    }

    private OrderResponse toOrderSummary(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .trackingNumber(order.getTrackingNumber())
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .build();
    }

    private OrderDetailResponse toOrderDetail(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() == null ? List.of() : order.getItems().stream()
                .map(this::toOrderItem)
                .toList();

        return OrderDetailResponse.orderDetailBuilder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .trackingNumber(order.getTrackingNumber())
                .itemCount(itemResponses.size())
                .items(itemResponses)
                .shippingAddress(order.getShippingAddress())
                .prescriptionId(order.getPrescription() != null ? order.getPrescription().getId() : null)
                .build();
    }

    private OrderItemResponse toOrderItem(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .medicineId(item.getMedicine() != null ? item.getMedicine().getId() : null)
                .medicineName(item.getMedicine() != null ? item.getMedicine().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}

