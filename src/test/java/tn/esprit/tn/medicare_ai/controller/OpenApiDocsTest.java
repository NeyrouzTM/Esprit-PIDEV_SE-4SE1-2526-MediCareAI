package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tn.medicare_ai.repository.AllergyRepository;
import tn.esprit.tn.medicare_ai.repository.AppointmentRepository;
import tn.esprit.tn.medicare_ai.repository.AvailabilityRepository;
import tn.esprit.tn.medicare_ai.repository.LabResultRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalImageRepository;
import tn.esprit.tn.medicare_ai.repository.MedicalRecordRepository;
import tn.esprit.tn.medicare_ai.repository.PrescriptionRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
class OpenApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$.info.title").value("Medicare AI API"))
                .andExpect(jsonPath("$.paths['/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/auth/register']").exists());
    }
}
