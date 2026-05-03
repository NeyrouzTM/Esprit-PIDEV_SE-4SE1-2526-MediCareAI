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
import tn.esprit.tn.medicare_ai.dto.response.MeetingResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.MeetingService;

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
class MeetingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private MeetingService meetingService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/meetings: returns list of meetings")
    void getAllMeetings_returnsList() throws Exception {
        MeetingResponseDTO meeting = MeetingResponseDTO.builder()
                .id(1L)
                .title("Test Meeting")
                .dateTime(LocalDateTime.now().plusDays(1))
                .meetingLink("https://meet.example.com/test")
                .organizerId(1L)
                .organizerName("Test Organizer")
                .recorded(false)
                .build();

        when(meetingService.getAllMeetings()).thenReturn(List.of(meeting));

        mockMvc.perform(get("/api/meetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Meeting"));
    }

    @Test
    @DisplayName("GET /api/meetings/{id}: valid id returns meeting")
    void getMeetingById_validId_returnsMeeting() throws Exception {
        MeetingResponseDTO meeting = MeetingResponseDTO.builder()
                .id(1L)
                .title("Test Meeting")
                .dateTime(LocalDateTime.now().plusDays(1))
                .meetingLink("https://meet.example.com/test")
                .organizerId(1L)
                .organizerName("Test Organizer")
                .recorded(false)
                .build();

        when(meetingService.getMeetingById(1L)).thenReturn(meeting);

        mockMvc.perform(get("/api/meetings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Meeting"));
    }
}




