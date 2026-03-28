package tn.esprit.tn.medicare_ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.tn.medicare_ai.dto.PrescriptionDTO;
import tn.esprit.tn.medicare_ai.entity.Prescription;
import tn.esprit.tn.medicare_ai.service.PrescriptionService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LegacyPrescriptionControllerTest {

    @Mock
    private PrescriptionService prescriptionService;

    @InjectMocks
    private PrescriptionController prescriptionController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(prescriptionController).build();
    }

    @Test
    @DisplayName("GET /prescriptions/{id} returns prescription")
    void getById_returnsPrescription() throws Exception {
        Prescription p = new Prescription();
        p.setId(5L);
        when(prescriptionService.getById(5L)).thenReturn(p);

        mockMvc.perform(get("/prescriptions/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("GET /prescriptions/medical-record/{id} returns list")
    void getByMedicalRecordId_returnsList() throws Exception {
        Prescription p = new Prescription();
        p.setId(9L);
        when(prescriptionService.getByMedicalRecordId(14L)).thenReturn(List.of(p));

        mockMvc.perform(get("/prescriptions/medical-record/14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9));
    }
}

