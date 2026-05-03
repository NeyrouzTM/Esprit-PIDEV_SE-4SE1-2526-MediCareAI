package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.tn.medicare_ai.dto.request.DrugInteractionCheckRequest;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionAlertDto;
import tn.esprit.tn.medicare_ai.dto.response.DrugInteractionCheckResponse;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
class InteractionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;


    @MockitoBean
    private DrugInteractionService drugInteractionService;

    @MockitoBean
    private MedicineService medicineService;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private RefillService refillService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private VerificationCodeRepository verificationCodeRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("POST /api/pharmacy/interactions/check: returns alerts when interactions exist")
    @WithMockUser(roles = "PATIENT")
    void checkInteractions_withAlerts_returnsAlerts() throws Exception {
        DrugInteractionCheckResponse response = DrugInteractionCheckResponse.builder()
                .alerts(List.of(DrugInteractionAlertDto.builder()
                        .medicineAName("Aspirin")
                        .medicineBName("Warfarin")
                        .severity("SEVERE")
                        .description("Bleeding")
                        .recommendation("Avoid")
                        .build()))
                .hasSevereInteraction(true)
                .build();

        when(drugInteractionService.checkInteractions(any())).thenReturn(response);

        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(1L, 2L))
                .build();

        mockMvc.perform(post("/api/pharmacy/interactions/check")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasSevereInteraction").value(true))
                .andExpect(jsonPath("$.alerts[0].medicineAName").value("Aspirin"));
    }

    @Test
    @DisplayName("POST /api/pharmacy/interactions/check: request with patientId is accepted")
    @WithMockUser(roles = "DOCTOR")
    void checkInteractions_withPatientId_returnsOk() throws Exception {
        when(drugInteractionService.checkInteractions(any()))
                .thenReturn(DrugInteractionCheckResponse.builder().alerts(List.of()).hasSevereInteraction(false).build());

        DrugInteractionCheckRequest request = DrugInteractionCheckRequest.builder()
                .medicineIds(List.of(1L, 2L))
                .patientId(77L)
                .build();

        mockMvc.perform(post("/api/pharmacy/interactions/check")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alerts.length()").value(0));
    }
}
