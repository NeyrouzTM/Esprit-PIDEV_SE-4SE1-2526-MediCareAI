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
import tn.esprit.tn.medicare_ai.dto.request.MedicineSearchRequest;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.entity.Medicine;
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
}

