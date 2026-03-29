package tn.esprit.tn.medicare_ai.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController controller;

    @Test
    void createPost_returnsCreated() {
        when(postService.createPost(any(PostRequestDTO.class), org.mockito.ArgumentMatchers.eq(2L))).thenReturn(new PostResponseDTO());

        ResponseEntity<PostResponseDTO> response = controller.createPost(new PostRequestDTO(), 2L);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void getAll_returnsOk() {
        when(postService.getAllPosts()).thenReturn(List.of(new PostResponseDTO()));

        ResponseEntity<List<PostResponseDTO>> response = controller.getAllPosts();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deletePost_returnsNoContent() {
        ResponseEntity<Void> response = controller.deletePost(10L, 2L);

        verify(postService).deletePost(10L, 2L);
        assertEquals(204, response.getStatusCode().value());
    }
}

