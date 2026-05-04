package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.LabResultRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalImageRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;
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

    @Test
    @DisplayName("OpenAPI grouped docs endpoint is available")
    void groupedDocsEndpoint_returnsOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/e-pharmacy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Medicare AI API"));
    }
}
