package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class OpenApiDocsTest {

    @Autowired
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

    @MockitoBean
    private VerificationCodeRepository verificationCodeRepository;

    @Test
    @DisplayName("OpenAPI grouped docs endpoint is available")
    void groupedDocsEndpoint_returnsOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/e-pharmacy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Medicare AI API"))
                .andExpect(jsonPath("$.paths['/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/auth/register']").exists());
    }
}
