package tn.esprit.tn.medicare_ai.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.Reply;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.ReplyRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.implementation.ReplyServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReplyServiceImpl replyService;

    @Test
    @DisplayName("createReply: valid request creates reply")
    void createReply_validRequest_createsReply() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        Post post = new Post();
        post.setId(1L);

        ReplyRequestDTO request = ReplyRequestDTO.builder()
                .content("Test reply content")
                .build();

        Reply savedReply = Reply.builder()
                .id(1L)
                .content("Test reply content")
                .post(post)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(replyRepository.save(any(Reply.class))).thenReturn(savedReply);

        ReplyResponseDTO result = replyService.createReply(request, 1L, 1L);

        assertEquals("Test reply content", result.getContent());
        assertEquals(1L, result.getAuthorId());
        verify(replyRepository).save(any(Reply.class));
    }

    @Test
    @DisplayName("getRepliesByPost: returns replies for post")
    void getRepliesByPost_returnsReplies() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        Post post = new Post();
        post.setId(1L);

        Reply reply = Reply.builder()
                .id(1L)
                .content("Test reply")
                .post(post)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(replyRepository.findByPostId(1L)).thenReturn(List.of(reply));

        List<ReplyResponseDTO> result = replyService.getRepliesByPost(1L);

        assertEquals(1, result.size());
        assertEquals("Test reply", result.get(0).getContent());
    }

    @Test
    @DisplayName("getReplyById: valid id returns reply")
    void getReplyById_validId_returnsReply() {
        User author = new User();
        author.setId(1L);
        author.setFullName("Test Author");

        Post post = new Post();
        post.setId(1L);

        Reply reply = Reply.builder()
                .id(1L)
                .content("Test reply")
                .post(post)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        when(replyRepository.findById(1L)).thenReturn(Optional.of(reply));

        ReplyResponseDTO result = replyService.getReplyById(1L);

        assertEquals("Test reply", result.getContent());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getReplyById: invalid id throws exception")
    void getReplyById_invalidId_throwsException() {
        when(replyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> replyService.getReplyById(1L));
    }
}
