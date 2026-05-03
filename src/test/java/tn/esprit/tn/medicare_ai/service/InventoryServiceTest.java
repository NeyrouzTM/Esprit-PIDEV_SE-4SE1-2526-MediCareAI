package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import tn.esprit.tn.medicare_ai.dto.request.InventoryUpdateRequest;
import tn.esprit.tn.medicare_ai.dto.response.InventoryResponse;
import tn.esprit.tn.medicare_ai.entity.Inventory;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.InventoryRepository;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("updateStock: updates existing medicine stock")
    void updateStock_existingMedicine_updatesStock() {
        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Paracetamol");

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setMedicine(medicine);
        inventory.setStockQuantity(5);
        inventory.setWarehouseLocation("MAIN");

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .medicineId(1L)
                .stockQuantity(20)
                .build();

        InventoryResponse response = inventoryService.updateStock(request);

        assertEquals(20, response.getStockQuantity());
        assertEquals("Paracetamol", response.getMedicineName());
    }

    @Test
    @DisplayName("updateStock: medicine not found throws ResourceNotFoundException")
    void updateStock_medicineNotFound_throws() {
        when(medicineRepository.findById(55L)).thenReturn(Optional.empty());

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .medicineId(55L)
                .stockQuantity(10)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.updateStock(request));
    }

    @Test
    @DisplayName("updateStock: optimistic locking conflict is propagated")
    void updateStock_optimisticLocking_throws() {
        Medicine medicine = new Medicine();
        medicine.setId(1L);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setMedicine(medicine);

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Inventory.class, 1L));

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .medicineId(1L)
                .stockQuantity(30)
                .build();

        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> inventoryService.updateStock(request));
    }
}



