package tn.esprit.tn.medicare_ai.service.implementation;

import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.entity.Post;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.PostRepository;
import tn.esprit.tn.medicare_ai.repository.ReplyRepository;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.ContentModerationService;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final ContentModerationService moderationService;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           ReplyRepository replyRepository,
                           ContentModerationService moderationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.replyRepository = replyRepository;
        this.moderationService = moderationService;
    }

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO dto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé"));

        if (Boolean.TRUE.equals(dto.getIsPremiumOnly()) && !author.isPremium()) {
            throw new IllegalArgumentException("Seuls les premium peuvent créer des posts premium");
        }

        // Modération du contenu
        moderationService.checkContent(dto.getTitle());
        moderationService.checkContent(dto.getContent());

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .tags(dto.getTags() != null ? new HashSet<>(dto.getTags()) : new HashSet<>())
                .isPremiumOnly(Boolean.TRUE.equals(dto.getIsPremiumOnly()))
                .build();

        Post saved = postRepository.save(post);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAllWithLikes().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> getAllPosts(Boolean premiumOnly, String sortBy) {
        List<Post> posts;

        if (Boolean.TRUE.equals(premiumOnly)) {
            posts = postRepository.findByIsPremiumOnlyTrue();
        } else if (Boolean.FALSE.equals(premiumOnly)) {
            posts = postRepository.findByIsPremiumOnlyFalse();
        } else {
            posts = postRepository.findAllWithLikes();
        }

        posts = posts.stream()
                .filter(p -> p.getCreatedAt() != null)
                .sorted((a, b) -> {
                    if ("oldest".equalsIgnoreCase(sortBy)) {
                        return a.getCreatedAt().compareTo(b.getCreatedAt());
                    } else {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                })
                .collect(Collectors.toList());


        return posts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + id + " non trouvé"));
        // Incrémenter le compteur de vues
        post.setViewsCount(post.getViewsCount() + 1);
        postRepository.save(post);
        return mapToResponseDTO(post);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO dto, Long currentUserId) {
        Post post = postRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + id + " non trouvé"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Non autorisé à modifier ce post");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setTags(dto.getTags() != null ? new HashSet<>(dto.getTags()) : post.getTags());
        post.setPremiumOnly(Boolean.TRUE.equals(dto.getIsPremiumOnly()));

        // Modération du contenu modifié
        moderationService.checkContent(dto.getTitle());
        moderationService.checkContent(dto.getContent());

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

    @Override
    @Transactional
    public PostResponseDTO toggleLike(Long postId, Long userId) {
        Post post = postRepository.findByIdWithLikes(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post avec ID " + postId + " non trouvé"));

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur avec ID " + userId + " non trouvé"));

        List<Long> likes = post.getLikedByUserIds();

        if (likes == null) {
            likes = new ArrayList<>();
            post.setLikedByUserIds(likes);
        }

        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            likes.add(userId);
        }

        Post saved = postRepository.save(post);
        return mapToResponseDTOForUser(saved, userId);
    }

    // ── Recommandations ───────────────────────────────────────────────────────

    @Override
    public List<PostResponseDTO> getRecommendations(Long userId, int limit) {

        // 1. Récupérer les posts likés par l'utilisateur
        List<Post> allPosts = postRepository.findAllWithLikes();
        List<Post> likedPosts = allPosts.stream()
                .filter(p -> p.getLikedByUserIds() != null && p.getLikedByUserIds().contains(userId))
                .collect(Collectors.toList());

        // 2. Extraire les tags préférés de l'utilisateur (depuis ses likes)
        Set<String> preferredTags = likedPosts.stream()
                .filter(p -> p.getTags() != null)
                .flatMap(p -> p.getTags().stream())
                .collect(Collectors.toSet());

        // 3. IDs des posts déjà likés (à exclure ou déprioriser)
        Set<Long> likedPostIds = likedPosts.stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

        // 4. Scorer chaque post
        LocalDateTime now = LocalDateTime.now();
        Map<Post, Double> scores = new HashMap<>();

        for (Post post : allPosts) {
            // Ne pas recommander ses propres posts
            if (post.getAuthor().getId().equals(userId)) continue;

            double score = 0.0;

            // A) Score tags (40%) — +2 par tag en commun avec les préférences
            if (preferredTags != null && !preferredTags.isEmpty() && post.getTags() != null) {
                long commonTags = post.getTags().stream()
                        .filter(preferredTags::contains)
                        .count();
                score += commonTags * 2.0;
            }

            // B) Score popularité (30%) — basé sur le nombre de likes
            int likesCount = post.getLikedByUserIds() != null ? post.getLikedByUserIds().size() : 0;
            score += Math.log1p(likesCount) * 1.5;

            // C) Score récence (20%) — posts récents favorisés
            long daysOld = ChronoUnit.DAYS.between(
                    post.getCreatedAt() != null ? post.getCreatedAt() : now, now);
            score += Math.max(0, 10.0 - daysOld * 0.5);

            // D) Score vues (10%) — posts populaires en vues
            score += Math.log1p(post.getViewsCount()) * 0.5;

            // E) Bonus si non encore liké par l'utilisateur
            if (!likedPostIds.contains(post.getId())) {
                score += 1.0;
            }

            scores.put(post, score);
        }

        // 5. Trier par score décroissant et retourner les `limit` premiers
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> mapToResponseDTOForUser(e.getKey(), userId))
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private PostResponseDTO mapToResponseDTO(Post post) {
        List<Long> likes = post.getLikedByUserIds();
        long repliesCount = replyRepository.countByPostId(post.getId());
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getFullName())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags())
                .isPremiumOnly(post.isPremiumOnly())
                .likesCount(likes != null ? likes.size() : 0)
                .likedByCurrentUser(false)
                .repliesCount((int) repliesCount)
                .viewsCount(post.getViewsCount())
                .build();
    }

    private PostResponseDTO mapToResponseDTOForUser(Post post, Long userId) {
        List<Long> likes = post.getLikedByUserIds();
        long repliesCount = replyRepository.countByPostId(post.getId());
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getFullName())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags())
                .isPremiumOnly(post.isPremiumOnly())
                .likesCount(likes != null ? likes.size() : 0)
                .likedByCurrentUser(likes != null && likes.contains(userId))
                .repliesCount((int) repliesCount)
                .viewsCount(post.getViewsCount())
                .build();
    }
}
