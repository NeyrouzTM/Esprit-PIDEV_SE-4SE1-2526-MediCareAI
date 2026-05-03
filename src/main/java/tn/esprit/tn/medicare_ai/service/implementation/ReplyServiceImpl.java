package tn.esprit.tn.medicare_ai.service.implementation;

import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Reply;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.ReplyRepository;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.ContentModerationService;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ContentModerationService moderationService;

    public ReplyServiceImpl(ReplyRepository replyRepository,
                            PostRepository postRepository,
                            UserRepository userRepository,
                            ContentModerationService moderationService) {
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.moderationService = moderationService;
    }

    @Override
    @Transactional
    public ReplyResponseDTO createReply(ReplyRequestDTO dto, Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouve"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouve"));
        Reply reply = Reply.builder()
                .content(dto.getContent())
                .post(post)
                .author(author)
                .build();

        // Modération du contenu
        moderationService.checkContent(dto.getContent());
        Reply saved = replyRepository.save(reply);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<ReplyResponseDTO> getRepliesByPost(Long postId) {
        return replyRepository.findByPostId(postId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReplyResponseDTO getReplyById(Long id) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reponse avec ID " + id + " non trouvee"));
        return mapToResponseDTO(reply);
    }

    @Override
    @Transactional
    public ReplyResponseDTO updateReply(Long id, ReplyRequestDTO dto, Long currentUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reponse non trouvee"));
        if (!reply.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorise a modifier cette reponse");
        }
        // Modération du contenu modifié
        moderationService.checkContent(dto.getContent());
        reply.setContent(dto.getContent());
        return mapToResponseDTO(replyRepository.save(reply));
    }

    @Override
    @Transactional
    public void deleteReply(Long id, Long currentUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reponse non trouvee"));
        if (!reply.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorise a supprimer cette reponse");
        }
        replyRepository.delete(reply);
    }

    @Override
    @Transactional
    public ReplyResponseDTO toggleLike(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reponse avec ID " + replyId + " non trouvee"));
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur avec ID " + userId + " non trouve"));
        List<Long> likes = reply.getLikedByUserIds();
        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            likes.add(userId);
        }
        Reply saved = replyRepository.save(reply);
        return mapToResponseDTOForUser(saved, userId);
    }

    private ReplyResponseDTO mapToResponseDTO(Reply reply) {
        List<Long> likes = reply.getLikedByUserIds();
        return ReplyResponseDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .postId(reply.getPost().getId())
                .authorId(reply.getAuthor().getId())
                .authorName(reply.getAuthor().getFullName())
                .createdAt(reply.getCreatedAt())
                .likesCount(likes != null ? likes.size() : 0)
                .likedByCurrentUser(false)
                .build();
    }

    private ReplyResponseDTO mapToResponseDTOForUser(Reply reply, Long userId) {
        List<Long> likes = reply.getLikedByUserIds();
        return ReplyResponseDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .postId(reply.getPost().getId())
                .authorId(reply.getAuthor().getId())
                .authorName(reply.getAuthor().getFullName())
                .createdAt(reply.getCreatedAt())
                .likesCount(likes != null ? likes.size() : 0)
                .likedByCurrentUser(likes != null && likes.contains(userId))
                .build();
    }
}
