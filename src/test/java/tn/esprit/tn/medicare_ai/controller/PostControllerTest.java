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
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/forum/posts: returns list of posts")
    void getAllPosts_returnsList() throws Exception {
        PostResponseDTO post = PostResponseDTO.builder()
                .id(1L)
                .title("Test Post")
                .content("Test content")
                .authorId(1L)
                .authorName("Test Author")
                .createdAt(LocalDateTime.now())
                .tags(Set.of("tag1", "tag2"))
                .isPremiumOnly(false)
                .build();

        when(postService.getAllPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/forum/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("GET /api/forum/posts/{id}: valid id returns post")
    void getPostById_validId_returnsPost() throws Exception {
        PostResponseDTO post = PostResponseDTO.builder()
                .id(1L)
                .title("Test Post")
                .content("Test content")
                .authorId(1L)
                .authorName("Test Author")
                .createdAt(LocalDateTime.now())
                .tags(Set.of("tag1", "tag2"))
                .isPremiumOnly(false)
                .build();

        when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(get("/api/forum/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }
}
