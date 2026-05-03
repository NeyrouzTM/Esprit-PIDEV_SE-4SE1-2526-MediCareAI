package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.tn.medicare_ai.dto.request.PrescriptionRequest;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionDetailResponse;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;
import tn.esprit.tn.medicare_ai.service.DrugInteractionService;
import tn.esprit.tn.medicare_ai.service.InventoryService;
import tn.esprit.tn.medicare_ai.service.MedicineService;
import tn.esprit.tn.medicare_ai.service.OrderService;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;
import tn.esprit.tn.medicare_ai.service.RefillService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
class PrescriptionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private PrescriptionService prescriptionService;

    @MockBean
    private MedicineService medicineService;

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

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("POST /api/pharmacy/prescriptions: doctor creates prescription successfully")
    @WithMockUser(username = "doctor@med.com", roles = "DOCTOR")
    void createPrescription_validDoctor_returnsCreated() throws Exception {
        User doctor = new User();
        doctor.setId(20L);
        doctor.setEmail("doctor@med.com");
        doctor.setRole(Role.DOCTOR);

        when(userRepository.findByEmail("doctor@med.com")).thenReturn(Optional.of(doctor));
        when(prescriptionService.createPrescription(any(PrescriptionRequest.class), eq(20L)))
                .thenReturn(PrescriptionDetailResponse.prescriptionDetailBuilder().id(100L).build());

        String validRequestJson = """
                {
                  "patientId": 10,
                  "expiryDate": "2099-12-31",
                  "items": [
                    {
                      "medicineId": 1,
                      "quantity": 1,
                      "dosage": "500mg",
                      "frequency": "daily",
                      "durationDays": 5
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/pharmacy/prescriptions")
                        .contentType(APPLICATION_JSON)
                        .content(validRequestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/pharmacy/prescriptions/100"));
    }

    @Test
    @DisplayName("POST /api/pharmacy/prescriptions: invalid payload returns 400")
    @WithMockUser(username = "doctor@med.com", roles = "DOCTOR")
    void createPrescription_invalidData_returnsBadRequest() throws Exception {
        User doctor = new User();
        doctor.setId(20L);
        doctor.setEmail("doctor@med.com");
        doctor.setRole(Role.DOCTOR);
        when(userRepository.findByEmail("doctor@med.com")).thenReturn(Optional.of(doctor));

        mockMvc.perform(post("/api/pharmacy/prescriptions")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/pharmacy/prescriptions: patient role is forbidden")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void createPrescription_patientForbidden_returns403() throws Exception {
        User patient = new User();
        patient.setId(30L);
        patient.setEmail("patient@med.com");
        patient.setRole(Role.PATIENT);
        when(userRepository.findByEmail("patient@med.com")).thenReturn(Optional.of(patient));

        when(prescriptionService.createPrescription(any(PrescriptionRequest.class), any(Long.class)))
                .thenThrow(new tn.esprit.tn.medicare_ai.exception.UnauthorizedActionException("forbidden"));

        String validRequestJson = """
                {
                  "patientId": 10,
                  "expiryDate": "2099-12-31",
                  "items": [
                    {
                      "medicineId": 1,
                      "quantity": 1,
                      "dosage": "500mg",
                      "frequency": "daily",
                      "durationDays": 5
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/pharmacy/prescriptions")
                        .contentType(APPLICATION_JSON)
                        .content(validRequestJson))
                .andExpect(status().isForbidden());
    }
}




