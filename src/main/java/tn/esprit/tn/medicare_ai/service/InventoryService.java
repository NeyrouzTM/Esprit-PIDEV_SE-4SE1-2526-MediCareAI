package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tn.medicare_ai.dto.request.InventoryUpdateRequest;
import tn.esprit.tn.medicare_ai.dto.response.InventoryResponse;
import tn.esprit.tn.medicare_ai.entity.Inventory;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.InventoryRepository;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public InventoryResponse updateStock(InventoryUpdateRequest request) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + request.getMedicineId()));

        Inventory inventory = inventoryRepository.findByMedicineId(medicine.getId())
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setMedicine(medicine);
                    newInventory.setWarehouseLocation("MAIN");
                    return newInventory;
                });

        inventory.setStockQuantity(request.getStockQuantity());
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryStatus() {
        return inventoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void deleteInventoryByMedicine(Long medicineId) {
        Inventory inventory = inventoryRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for medicine: " + medicineId));
        inventoryRepository.delete(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .medicineId(inventory.getMedicine() != null ? inventory.getMedicine().getId() : null)
                .medicineName(inventory.getMedicine() != null ? inventory.getMedicine().getName() : null)
                .stockQuantity(inventory.getStockQuantity())
                .warehouseLocation(inventory.getWarehouseLocation())
                .build();
    }
}
