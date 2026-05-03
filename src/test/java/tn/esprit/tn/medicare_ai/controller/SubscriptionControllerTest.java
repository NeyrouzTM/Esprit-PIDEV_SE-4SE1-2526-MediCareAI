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
import tn.esprit.tn.medicare_ai.dto.response.SubscriptionResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

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
class SubscriptionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/subscriptions/user/{userId}: returns list of subscriptions")
    void getSubscriptionsByUser_returnsList() throws Exception {
        SubscriptionResponseDTO subscription = SubscriptionResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .planId(1L)
                .planName("Basic Plan")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status("ACTIVE")
                .activeNow(true)
                .build();

        when(subscriptionService.getSubscriptionsByUser(1L)).thenReturn(List.of(subscription));

        mockMvc.perform(get("/api/subscriptions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activeNow").value(true));
    }

    @Test
    @DisplayName("GET /api/subscriptions/{id}: valid id returns subscription")
    void getSubscriptionById_validId_returnsSubscription() throws Exception {
        SubscriptionResponseDTO subscription = SubscriptionResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .planId(1L)
                .planName("Basic Plan")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status("ACTIVE")
                .activeNow(true)
                .build();

        when(subscriptionService.getSubscriptionById(1L)).thenReturn(subscription);

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeNow").value(true));
    }
}




