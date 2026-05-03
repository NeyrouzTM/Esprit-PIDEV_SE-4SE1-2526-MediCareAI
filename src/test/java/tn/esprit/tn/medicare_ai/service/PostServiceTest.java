package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.ReplyRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.PostServiceImpl;
import tn.esprit.tn.medicare_ai.service.interfaces.ContentModerationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private ContentModerationService moderationService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("createPost: valid request creates post")
    void createPost_validRequest_createsPost() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");
        author.setPremium(true);

        PostRequestDTO request = PostRequestDTO.builder()
                .title("Test Title")
                .content("Test content with enough characters")
                .tags(List.of("tag1"))
                .isPremiumOnly(false)
                .build();

        Post savedPost = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test content with enough characters")
                .author(author)
                .tags(Set.of("tag1"))
                .isPremiumOnly(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(replyRepository.countByPostId(anyLong())).thenReturn(0L);

        PostResponseDTO result = postService.createPost(request, 1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals(1L, result.getAuthorId());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("getAllPosts: returns all posts")
    void getAllPosts_returnsAllPosts() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        Post post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test content")
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findAllWithLikes()).thenReturn(List.of(post));
        when(replyRepository.countByPostId(anyLong())).thenReturn(0L);

        List<PostResponseDTO> result = postService.getAllPosts();

        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    @DisplayName("getPostById: valid id returns post")
    void getPostById_validId_returnsPost() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        Post post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test content")
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findByIdWithLikes(1L)).thenReturn(Optional.of(post));
        when(replyRepository.countByPostId(anyLong())).thenReturn(0L);

        PostResponseDTO result = postService.getPostById(1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getPostById: invalid id throws exception")
    void getPostById_invalidId_throwsException() {
        when(postRepository.findByIdWithLikes(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> postService.getPostById(1L));
    }
}
