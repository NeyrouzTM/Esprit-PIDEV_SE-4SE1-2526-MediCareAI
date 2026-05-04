package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.tn.medicare_ai.dto.response.MedicineDetailResponse;
import tn.esprit.tn.medicare_ai.dto.response.MedicineResponse;
import tn.esprit.tn.medicare_ai.exception.ResourceNotFoundException;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.LabResultRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalImageRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    @MockBean
    private MedicineService medicineService;

    @MockBean
    private PrescriptionService prescriptionService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private DrugInteractionService drugInteractionService;

    @MockBean
    private RefillService refillService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VerificationCodeRepository verificationCodeRepository;

    @MockitoBean
    private AllergyRepository allergyRepository;

    @MockitoBean
    private AppointmentRepository appointmentRepository;

    @MockitoBean
    private AvailabilityRepository availabilityRepository;

    @MockitoBean
    private LabResultRepository labResultRepository;

    @MockitoBean
    private MedicalImageRepository medicalImageRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    @MockitoBean
    private PrescriptionRepository prescriptionRepository;

    @MockitoBean
    private VisitNoteRepository visitNoteRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/pharmacy/medicines: valid search returns 200 with content")
    void searchMedicines_validSearch_returnsPage() throws Exception {
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

    @Test
    @DisplayName("POST /api/pharmacy/medicines: pharmacist can create medicine")
    @WithMockUser(username = "pharmacist@med.com", roles = "PHARMACIST")
    void createMedicine_pharmacist_returnsCreated() throws Exception {
        when(medicineService.createMedicine(any())).thenReturn(
                MedicineDetailResponse.medicineDetailBuilder()
                        .id(99L)
                        .name("Ibuprofen")
                        .build()
        );

        String payload = """
                {
                  "name": "Ibuprofen",
                  "genericName": "Ibuprofen",
                  "manufacturer": "Pfizer",
                  "description": "Anti-inflammatory",
                  "category": "ANALGESIC",
                  "dosageForm": "Tablet",
                  "strength": "400mg",
                  "imageUrl": "https://cdn.example.com/ibuprofen.png",
                  "price": 12.5,
                  "prescriptionRequired": false
                }
                """;

        mockMvc.perform(post("/api/pharmacy/medicines")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/pharmacy/medicines/99"))
                .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    @DisplayName("POST /api/pharmacy/medicines: patient is forbidden")
    void createMedicine_patient_forbidden() throws Exception {
        String payload = """
                {
                  "name": "Ibuprofen",
                  "category": "ANALGESIC",
                  "dosageForm": "Tablet",
                  "strength": "400mg",
                  "price": 12.5,
                  "prescriptionRequired": false
                }
                """;

        mockMvc.perform(post("/api/pharmacy/medicines")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/pharmacy/medicines: duplicate name returns 400")
    @WithMockUser(username = "pharmacist@med.com", roles = "PHARMACIST")
    void createMedicine_duplicateName_returnsBadRequest() throws Exception {
        when(medicineService.createMedicine(any())).thenThrow(new IllegalArgumentException("Medicine with name already exists"));

        String payload = """
                {
                  "name": "Ibuprofen",
                  "category": "ANALGESIC",
                  "dosageForm": "Tablet",
                  "strength": "400mg",
                  "price": 12.5,
                  "prescriptionRequired": false
                }
                """;

        mockMvc.perform(post("/api/pharmacy/medicines")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/pharmacy/medicines/{id}: pharmacist can update medicine")
    @WithMockUser(username = "pharmacist@med.com", roles = "PHARMACIST")
    void updateMedicine_pharmacist_returnsOk() throws Exception {
        when(medicineService.updateMedicine(any(Long.class), any())).thenReturn(
                MedicineDetailResponse.medicineDetailBuilder().id(10L).name("Updated Med").build()
        );

        String payload = """
                {
                  "name": "Updated Med",
                  "category": "ANALGESIC",
                  "dosageForm": "Tablet",
                  "strength": "500mg",
                  "price": 10.0,
                  "prescriptionRequired": false
                }
                """;

        mockMvc.perform(put("/api/pharmacy/medicines/10")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("PUT /api/pharmacy/medicines/{id}: not found returns 404")
    @WithMockUser(username = "pharmacist@med.com", roles = "PHARMACIST")
    void updateMedicine_notFound_returns404() throws Exception {
        when(medicineService.updateMedicine(any(Long.class), any()))
                .thenThrow(new ResourceNotFoundException("Medicine not found"));

        String payload = """
                {
                  "name": "Updated Med",
                  "category": "ANALGESIC",
                  "dosageForm": "Tablet",
                  "strength": "500mg",
                  "price": 10.0,
                  "prescriptionRequired": false
                }
                """;

        mockMvc.perform(put("/api/pharmacy/medicines/999")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/pharmacy/medicines/{id}: pharmacist can delete medicine")
    @WithMockUser(username = "pharmacist@med.com", roles = "PHARMACIST")
    void deleteMedicine_pharmacist_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/pharmacy/medicines/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/pharmacy/medicines/{id}: patient is forbidden")
    void deleteMedicine_patient_forbidden() throws Exception {
        mockMvc.perform(delete("/api/pharmacy/medicines/10"))
                .andExpect(status().isForbidden());
    }
}




