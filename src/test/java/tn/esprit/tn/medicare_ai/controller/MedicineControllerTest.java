package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@WithMockUser(username = "patient@med.com", roles = "PATIENT")
class MedicineControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private MedicineService medicineService;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private DrugInteractionService drugInteractionService;

    @MockitoBean
    private RefillService refillService;

    @MockitoBean
    private UserRepository userRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        MedicineResponse med = MedicineResponse.builder()
                .id(1L)
                .name("Paracetamol")
                .genericName("Paracetamol")
                .dosageForm("Tablet")
                .strength("500mg")
                .price(5.0)
                .prescriptionRequired(false)
                .imageUrl(null)
                .build();
        Page<MedicineResponse> page = new PageImpl<>(List.of(med));

        when(medicineService.searchMedicines(any())).thenReturn(page);

        mockMvc.perform(get("/api/pharmacy/medicines").param("keyword", "para"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Paracetamol"));
    }

    @Test
    @DisplayName("GET /api/pharmacy/medicines: no results returns empty page")
    void searchMedicines_noResults_returnsEmptyPage() throws Exception {
        when(medicineService.searchMedicines(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/pharmacy/medicines").param("keyword", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}
