package tn.esprit.tn.medicare_ai.service.implementation;
import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Reply;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.ReplyRepository;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReplyServiceImpl(ReplyRepository replyRepository,
                            PostRepository postRepository,
                            UserRepository userRepository) {
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReplyResponseDTO createReply(ReplyRequestDTO dto, Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trouvé"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));

        Reply reply = Reply.builder()
                .content(dto.getContent())
                .post(post)
                .author(author)
                .build();

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
                .orElseThrow(() -> new EntityNotFoundException("Réponse avec ID " + id + " non trouvée"));
        return mapToResponseDTO(reply);
    }

    @Override
    @Transactional
    public ReplyResponseDTO updateReply(Long id, ReplyRequestDTO dto, Long currentUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réponse non trouvée"));

        if (!reply.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorisé à modifier cette réponse");
        }

        reply.setContent(dto.getContent());

        Reply updated = replyRepository.save(reply);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReply(Long id, Long currentUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réponse non trouvée"));

        if (!reply.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorisé à supprimer cette réponse");
        }

        replyRepository.delete(reply);
    }


    private ReplyResponseDTO mapToResponseDTO(Reply reply) {
        return ReplyResponseDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .postId(reply.getPost().getId())
                .authorId(reply.getAuthor().getId())
                .authorName(reply.getAuthor().getFullName())
                .createdAt(reply.getCreatedAt())
                .build();
    }}
