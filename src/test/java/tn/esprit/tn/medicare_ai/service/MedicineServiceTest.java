package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tn.esprit.tn.medicare_ai.dto.request.CreateMedicineRequest;
import tn.esprit.tn.medicare_ai.dto.request.MedicineSearchRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicineDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.entity.MedicinieCategory;
import tn.esprit.tn.medicare_ai.entity.Medicine;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.DrugInteractionRepository;
import tn.esprit.tn.medicare_ai.repository.MedicineRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private DrugInteractionRepository drugInteractionRepository;

    @InjectMocks
    private MedicineService medicineService;

    @Test
    @DisplayName("searchMedicines: keyword match returns paged medicines")
    void searchMedicines_keywordMatch_returnsPage() {
        Medicine m = new Medicine();
        m.setId(1L);
        m.setName("Paracetamol");
        m.setGenericName("Acetaminophen");
        m.setPrice(10.0);

        Page<Medicine> page = new PageImpl<>(List.of(m));
        when(medicineRepository.search(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        MedicineSearchRequest request = MedicineSearchRequest.builder()
                .keyword("para")
                .page(0)
                .size(10)
                .build();

        Page<MedicineResponse> result = medicineService.searchMedicines(request);

        assertEquals(1, result.getTotalElements());
        assertEquals("Paracetamol", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("searchMedicines: no match returns empty page")
    void searchMedicines_noMatch_returnsEmptyPage() {
        when(medicineRepository.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        MedicineSearchRequest request = MedicineSearchRequest.builder()
                .keyword("not-found")
                .page(0)
                .size(10)
                .build();

        Page<MedicineResponse> result = medicineService.searchMedicines(request);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("searchMedicines: pagination params are passed correctly")
    void searchMedicines_paginationPassedCorrectly() {
        when(medicineRepository.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        MedicineSearchRequest request = MedicineSearchRequest.builder()
                .keyword("amox")
                .page(2)
                .size(5)
                .build();

        medicineService.searchMedicines(request);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(medicineRepository).search(eq("amox"), isNull(), isNull(), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertEquals(PageRequest.of(2, 5), captured);
    }

    @Test
    @DisplayName("createMedicine: saves medicine and returns detail response")
    void createMedicine_savesAndReturnsResponse() {
        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .name("Ibuprofen")
                .genericName("Ibuprofen")
                .manufacturer("Pfizer")
                .description("Anti-inflammatory")
                .category(MedicinieCategory.ANALGESIC)
                .dosageForm("Tablet")
                .strength("400mg")
                .imageUrl("https://cdn.example.com/ibuprofen.png")
                .price(12.5)
                .prescriptionRequired(false)
                .build();

        when(medicineRepository.save(any(Medicine.class))).thenAnswer(invocation -> {
            Medicine saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        MedicineDetailResponse response = medicineService.createMedicine(request);

        assertEquals(99L, response.getId());
        assertEquals("Ibuprofen", response.getName());
        assertEquals("Pfizer", response.getManufacturer());

        ArgumentCaptor<Medicine> medicineCaptor = ArgumentCaptor.forClass(Medicine.class);
        verify(medicineRepository).save(medicineCaptor.capture());
        assertEquals(MedicinieCategory.ANALGESIC, medicineCaptor.getValue().getCategory());
    }

    @Test
    @DisplayName("createMedicine: duplicate name throws bad request")
    void createMedicine_duplicateName_throwsIllegalArgument() {
        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .name("Ibuprofen")
                .category(MedicinieCategory.ANALGESIC)
                .dosageForm("Tablet")
                .strength("400mg")
                .price(12.5)
                .prescriptionRequired(false)
                .build();

        when(medicineRepository.existsByNameIgnoreCase("Ibuprofen")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> medicineService.createMedicine(request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(medicineRepository, never()).save(any(Medicine.class));
    }

    @Test
    @DisplayName("updateMedicine: updates existing medicine")
    void updateMedicine_existing_updatesAndReturns() {
        Medicine existing = new Medicine();
        existing.setId(5L);
        existing.setName("Old Name");

        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .name("New Name")
                .genericName("New Generic")
                .manufacturer("New Mfg")
                .description("Updated")
                .category(MedicinieCategory.OTHER)
                .dosageForm("Capsule")
                .strength("20mg")
                .price(6.0)
                .prescriptionRequired(true)
                .build();

        when(medicineRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));
        when(medicineRepository.existsByNameIgnoreCaseAndIdNot("New Name", 5L)).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenAnswer(i -> i.getArgument(0));

        MedicineDetailResponse response = medicineService.updateMedicine(5L, request);

        assertEquals(5L, response.getId());
        assertEquals("New Name", response.getName());
        assertEquals("New Mfg", response.getManufacturer());
    }

    @Test
    @DisplayName("updateMedicine: not found throws ResourceNotFoundException")
    void updateMedicine_notFound_throwsResourceNotFound() {
        when(medicineRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .name("Any")
                .category(MedicinieCategory.OTHER)
                .dosageForm("Tablet")
                .strength("10mg")
                .price(1.0)
                .prescriptionRequired(false)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> medicineService.updateMedicine(99L, request));
    }

    @Test
    @DisplayName("deleteMedicine: existing id deletes")
    void deleteMedicine_existing_deletes() {
        when(medicineRepository.existsById(8L)).thenReturn(true);

        medicineService.deleteMedicine(8L);

        verify(medicineRepository).deleteById(8L);
    }

    @Test
    @DisplayName("deleteMedicine: missing id throws ResourceNotFoundException")
    void deleteMedicine_missing_throwsResourceNotFound() {
        when(medicineRepository.existsById(404L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> medicineService.deleteMedicine(404L));
        verify(medicineRepository, never()).deleteById(anyLong());
    }
}
