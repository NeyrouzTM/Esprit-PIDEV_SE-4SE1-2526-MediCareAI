package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.tn.medicare_ai.entity.Appointment;
import tn.esprit.tn.medicare_ai.service.AppointmentService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
    }

    @Test
    @DisplayName("GET /appointments returns list")
    void getAll_returnsList() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        when(appointmentService.getAll()).thenReturn(List.of(appointment));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /appointments/{id}/teleconsultation/start returns started status")
    void startTeleconsultation_returnsStarted() throws Exception {
        mockMvc.perform(post("/appointments/9/teleconsultation/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(9))
                .andExpect(jsonPath("$.status").value("STARTED"));
    }
}





