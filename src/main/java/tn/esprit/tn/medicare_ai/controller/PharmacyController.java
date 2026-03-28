package tn.esprit.tn.medicare_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.esprit.tn.medicare_ai.dto.request.*;
import tn.esprit.tn.medicare_ai.dto.response.*;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "E-Pharmacy", description = "Endpoints for medicine search, prescriptions, orders, inventory, refills, and drug interactions.")
@SecurityRequirement(name = "bearerAuth")
public class PharmacyController {

    private final MedicineService medicineService;
    private final PrescriptionService prescriptionService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final DrugInteractionService drugInteractionService;
    private final RefillService refillService;
    private final UserRepository userRepository;

    @GetMapping("/medicines")
    @Operation(summary = "Search medicines", description = "Returns a paginated list of medicines filtered by keyword, category, and prescription requirement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicines fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Page<MedicineResponse>> searchMedicines(@Valid @ParameterObject MedicineSearchRequest request) {
        return ResponseEntity.ok(medicineService.searchMedicines(request));
    }

    @PostMapping("/medicines")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Add medicine", description = "Adds a new medicine to the catalog. Pharmacist role required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicine created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid medicine payload", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only pharmacists can add medicines")
    })
    public ResponseEntity<MedicineDetailResponse> createMedicine(@Valid @RequestBody CreateMedicineRequest request) {
        MedicineDetailResponse created = medicineService.createMedicine(request);
        String location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUriString();
        return ResponseEntity.created(java.net.URI.create(location)).body(created);
    }

    @PutMapping("/medicines/{id}")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Update medicine", description = "Updates an existing medicine in the catalog. Pharmacist role required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update payload", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only pharmacists can update medicines"),
            @ApiResponse(responseCode = "404", description = "Medicine not found")
    })
    public ResponseEntity<MedicineDetailResponse> updateMedicine(
            @Parameter(description = "Medicine identifier", example = "10") @PathVariable Long id,
            @Valid @RequestBody CreateMedicineRequest request) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, request));
    }

    @DeleteMapping("/medicines/{id}")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Delete medicine", description = "Deletes a medicine from the catalog. Pharmacist role required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medicine deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only pharmacists can delete medicines"),
            @ApiResponse(responseCode = "404", description = "Medicine not found")
    })
    public ResponseEntity<Void> deleteMedicine(
            @Parameter(description = "Medicine identifier", example = "10") @PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medicines/{id}")
    @Operation(summary = "Get medicine details", description = "Returns detailed information for a medicine including known interaction alerts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Medicine not found", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class)))
    })
    public ResponseEntity<MedicineDetailResponse> getMedicineDetails(
            @Parameter(description = "Medicine identifier", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineDetail(id));
    }

    @PostMapping("/prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Create prescription", description = "Doctor issues a prescription with one or more medicine items for a patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prescription created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid prescription request", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only doctors can create prescriptions")
    })
    public ResponseEntity<PrescriptionDetailResponse> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        PrescriptionDetailResponse created = prescriptionService.createPrescription(request, getCurrentUserId());
        String location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUriString();
        return ResponseEntity.created(java.net.URI.create(location)).body(created);
    }

    @GetMapping("/prescriptions")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @Operation(summary = "List prescriptions", description = "Returns paginated prescriptions for the authenticated patient or doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied for current role")
    })
    public ResponseEntity<Page<PrescriptionResponse>> getPrescriptions(@ParameterObject Pageable pageable) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().name().equals("PATIENT")) {
            return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(currentUser.getId(), pageable));
        }
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(currentUser.getId(), pageable));
    }

    @GetMapping("/prescriptions/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @Operation(summary = "Get prescription details", description = "Returns full prescription details with all prescription items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Prescription not found", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class)))
    })
    public ResponseEntity<PrescriptionDetailResponse> getPrescriptionById(
            @Parameter(description = "Prescription identifier", example = "100") @PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Place order", description = "Patient places an order for medicines, optionally linked to a prescription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient stock", content = @Content(schema = @Schema(implementation = tn.esprit.tn.medicare_ai.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only patients can place orders")
    })
    public ResponseEntity<OrderDetailResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request, getCurrentUserId()));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get order history", description = "Returns paginated order history for the authenticated patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Only patients can access order history")
    })
    public ResponseEntity<Page<OrderResponse>> getOrderHistory(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderHistory(getCurrentUserId(), pageable));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get order by id", description = "Returns details for a specific patient order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied for this order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDetailResponse> getOrderById(
            @Parameter(description = "Order identifier", example = "200") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/orders/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Cancel order", description = "Cancels a patient order when cancellation is allowed by business rules.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDetailResponse> cancelOrder(
            @Parameter(description = "Order identifier", example = "200") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Delete order", description = "Permanently deletes a cancelled order owned by the authenticated patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Order does not belong to current patient or cannot be deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order identifier", example = "200") @PathVariable Long id) {
        orderService.deleteOrder(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Get inventory status", description = "Returns current medicine stock levels for pharmacists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Only pharmacists can view inventory")
    })
    public ResponseEntity<List<InventoryResponse>> getInventoryStatus() {
        return ResponseEntity.ok(inventoryService.getInventoryStatus());
    }

    @PutMapping("/inventory")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Update inventory", description = "Updates stock quantity for a medicine in inventory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid inventory update request"),
            @ApiResponse(responseCode = "404", description = "Medicine not found")
    })
    public ResponseEntity<InventoryResponse> updateInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateStock(request));
    }

    @DeleteMapping("/inventory/{medicineId}")
    @PreAuthorize("hasRole('PHARMACIST')")
    @Operation(summary = "Delete inventory entry", description = "Removes an inventory record for a medicine.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventory entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory entry not found")
    })
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Medicine identifier", example = "10") @PathVariable Long medicineId) {
        inventoryService.deleteInventoryByMedicine(medicineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/interactions/check")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','PHARMACIST')")
    @Operation(summary = "Check drug interactions", description = "Checks interactions between requested medicines and optionally includes patient context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid interaction check request")
    })
    public ResponseEntity<DrugInteractionCheckResponse> checkInteractions(@Valid @RequestBody DrugInteractionCheckRequest request) {
        return ResponseEntity.ok(drugInteractionService.checkInteractions(request));
    }

    @PostMapping("/refills")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Request refill", description = "Patient submits a refill request for an existing prescription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refill request submitted"),
            @ApiResponse(responseCode = "400", description = "Refill request rejected"),
            @ApiResponse(responseCode = "403", description = "Only patients can request refills")
    })
    public ResponseEntity<RefillResponse> requestRefill(@Valid @RequestBody RefillRequestDto request) {
        return ResponseEntity.ok(refillService.requestRefill(request, getCurrentUserId()));
    }

    @GetMapping("/refills")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get refill requests", description = "Returns paginated refill requests for the authenticated patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refill requests fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Only patients can access refill requests")
    })
    public ResponseEntity<Page<RefillResponse>> getRefills(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(refillService.getRefillsByPatient(getCurrentUserId(), pageable));
    }

    @DeleteMapping("/refills/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Delete refill request", description = "Deletes a pending refill request owned by the authenticated patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Refill request deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Refill request cannot be deleted by current patient"),
            @ApiResponse(responseCode = "404", description = "Refill request not found")
    })
    public ResponseEntity<Void> deleteRefill(
            @Parameter(description = "Refill request identifier", example = "55") @PathVariable Long id) {
        refillService.deleteRefill(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prescriptions/upload")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Upload prescription image", description = "Uploads a prescription image for verification before dispensing prescription-required medicines.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid upload request"),
            @ApiResponse(responseCode = "403", description = "Only patients can upload prescriptions")
    })
    public ResponseEntity<PrescriptionVerificationResponse> uploadPrescription(@Valid @ModelAttribute UploadPrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.uploadPrescription(request, getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
