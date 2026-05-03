package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionPlanResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionPlanService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
        }
)
@WithMockUser(username = "patient@med.com", roles = "PATIENT")
class SubscriptionPlanControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionPlanService planService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/subscription-plans: returns list of plans")
    void getAllPlans_returnsList() throws Exception {
        SubscriptionPlanResponseDTO plan = SubscriptionPlanResponseDTO.builder()
                .id(1L)
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        when(planService.getAllPlans()).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/subscription-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Basic Plan"));
    }

    @Test
    @DisplayName("GET /api/subscription-plans/{id}: valid id returns plan")
    void getPlanById_validId_returnsPlan() throws Exception {
        SubscriptionPlanResponseDTO plan = SubscriptionPlanResponseDTO.builder()
                .id(1L)
                .name("Basic Plan")
                .description("Basic subscription")
                .price(10.0)
                .durationDays(30)
                .build();

        when(planService.getPlanById(1L)).thenReturn(plan);

        mockMvc.perform(get("/api/subscription-plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Basic Plan"));
    }
}




