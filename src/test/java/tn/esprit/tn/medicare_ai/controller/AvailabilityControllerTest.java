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
import tn.esprit.tn.medicare_ai.entity.Availability;
import tn.esprit.tn.medicare_ai.service.AvailabilityService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController availabilityController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController).build();
    }

    @Test
    @DisplayName("GET /availabilities/doctor/{id} returns list")
    void getByDoctorId_returnsList() throws Exception {
        Availability a = new Availability();
        a.setId(2L);
        when(availabilityService.getByDoctorId(4L)).thenReturn(List.of(a));

        mockMvc.perform(get("/availabilities/doctor/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    @DisplayName("GET /availabilities/doctor/{id}/available returns list")
    void getAvailableSlots_returnsList() throws Exception {
        Availability a = new Availability();
        a.setId(3L);
        when(availabilityService.getAvailableSlots(4L)).thenReturn(List.of(a));

        mockMvc.perform(get("/availabilities/doctor/4/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }
}





