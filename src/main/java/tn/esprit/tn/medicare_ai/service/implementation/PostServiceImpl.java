package tn.esprit.tn.medicare_ai.service.implementation;
import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

@Override
@Transactional
public PostResponseDTO createPost(PostRequestDTO dto, Long authorId) {
    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));

    if (dto.isPremiumOnly() && !author.isPremium()) {
        throw new IllegalArgumentException("Seuls les premium peuvent créer des posts premium");
    }

    Post post = Post.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .author(author)
            .tags(dto.getTags() != null ? dto.getTags() : new ArrayList<>())
            .isPremiumOnly(dto.isPremiumOnly())
            .build();

    Post saved = postRepository.save(post);
    return mapToResponseDTO(saved);
}



    @Override
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + id + " non trouvé"));
        return mapToResponseDTO(post);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO dto, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + id + " non trouvé"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorisé à modifier ce post");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setTags(dto.getTags() != null ? dto.getTags() : post.getTags());
        post.setPremiumOnly(dto.isPremiumOnly());

        Post updated = postRepository.save(post);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + id + " non trouvé"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorisé à supprimer ce post");
        }

        postRepository.delete(post);
    }


private PostResponseDTO mapToResponseDTO(Post post) {
    return PostResponseDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .authorId(post.getAuthor().getId())
            .authorName(post.getAuthor().getFullName())
            .createdAt(post.getCreatedAt())
            .tags(post.getTags())
            .isPremiumOnly(post.isPremiumOnly())
            .build();
}}