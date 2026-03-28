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
import tn.esprit.tn.medicare_ai.dto.MedicalRecordDTO;
import tn.esprit.tn.medicare_ai.entity.MedicalRecord;
import tn.esprit.tn.medicare_ai.service.MedicalRecordService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
    }

    @Test
    @DisplayName("GET /medical-records returns list")
    void getAll_returnsList() throws Exception {
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        when(medicalRecordService.getAll()).thenReturn(List.of(record));

        mockMvc.perform(get("/medical-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /medical-records creates record")
    void create_returnsCreatedRecord() throws Exception {
        MedicalRecord saved = new MedicalRecord();
        saved.setId(11L);

        when(medicalRecordService.create(org.mockito.ArgumentMatchers.any(MedicalRecordDTO.class))).thenReturn(saved);

        MedicalRecordDTO dto = MedicalRecordDTO.builder().patientId(5L).bloodType("A+").build();

        mockMvc.perform(post("/medical-records")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));
    }
}

