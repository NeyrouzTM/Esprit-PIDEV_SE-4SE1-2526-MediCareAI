package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tn.medicare_ai.dto.request.RefillRequestDto;
import tn.esprit.tn.medicare_ai.dto.response.PrescriptionVerificationResponse;
import tn.esprit.tn.medicare_ai.dto.response.RefillResponse;
import tn.esprit.tn.medicare_ai.entity.RefillStatus;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VerificationCodeRepository;
import tn.esprit.tn.medicare_ai.service.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class RefillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RefillService refillService;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean
    private MedicineService medicineService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private DrugInteractionService drugInteractionService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private VerificationCodeRepository verificationCodeRepository;

    @Test
    @DisplayName("POST /api/pharmacy/refills: valid request returns refill details")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void requestRefill_valid_returnsOk() throws Exception {
        mockCurrentPatient();
        when(refillService.requestRefill(any(RefillRequestDto.class), eq(1L)))
                .thenReturn(RefillResponse.builder().id(9L).status(RefillStatus.PENDING).build());

        RefillRequestDto request = RefillRequestDto.builder()
                .prescriptionId(11L)
                .requestedQuantity(2)
                .build();

        mockMvc.perform(post("/api/pharmacy/refills")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/pharmacy/prescriptions/upload: uploads mock file and returns verification response")
    @WithMockUser(username = "patient@med.com", roles = "PATIENT")
    void uploadPrescription_mockFile_returnsVerification() throws Exception {
        mockCurrentPatient();

        when(prescriptionService.uploadPrescription(any(), eq(1L)))
                .thenReturn(PrescriptionVerificationResponse.builder()
                        .id(123L)
                        .status("PENDING_VERIFICATION")
                        .message("ok")
                        .build());

        mockMvc.perform(multipart("/api/pharmacy/prescriptions/upload")
                        .file("imageFile", "dummy-image".getBytes())
                        .param("doctorName", "Dr. Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
    }

    private void mockCurrentPatient() {
        User patient = new User();
        patient.setId(1L);
        patient.setEmail("patient@med.com");
        patient.setRole(Role.PATIENT);
        when(userRepository.findByEmail("patient@med.com")).thenReturn(Optional.of(patient));
    }
}
