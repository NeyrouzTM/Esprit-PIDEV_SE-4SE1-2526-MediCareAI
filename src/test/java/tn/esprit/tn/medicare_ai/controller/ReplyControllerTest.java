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
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;

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
class ReplyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private ReplyService replyService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/forum/posts/{postId}/replies: returns list of replies")
    void getRepliesByPost_returnsList() throws Exception {
        ReplyResponseDTO reply = ReplyResponseDTO.builder()
                .id(1L)
                .content("Test reply")
                .authorId(1L)
                .authorName("Test Author")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(replyService.getRepliesByPost(1L)).thenReturn(List.of(reply));

        mockMvc.perform(get("/api/forum/posts/1/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test reply"));
    }

    @Test
    @DisplayName("GET /api/forum/replies/{id}: valid id returns reply")
    void getReplyById_validId_returnsReply() throws Exception {
        ReplyResponseDTO reply = ReplyResponseDTO.builder()
                .id(1L)
                .content("Test reply")
                .authorId(1L)
                .authorName("Test Author")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(replyService.getReplyById(1L)).thenReturn(reply);

        mockMvc.perform(get("/api/forum/replies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test reply"));
    }
}




