package tn.esprit.tn.medicare_ai.controller;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/posts")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostRequestDTO dto,
            @RequestParam Long authorId) {

        PostResponseDTO created = postService.createPost(dto, authorId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts(
            @RequestParam(required = false) Boolean premiumOnly,
            @RequestParam(required = false) String sortBy) {

        if (premiumOnly == null && sortBy == null) {
            return ResponseEntity.ok(postService.getAllPosts());
        }

        return ResponseEntity.ok(postService.getAllPosts(
                premiumOnly != null ? premiumOnly : false,
                sortBy != null ? sortBy : "newest"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDTO dto,
            @RequestParam Long currentUserId) {

        return ResponseEntity.ok(postService.updatePost(id, dto, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestParam Long currentUserId) {

        postService.deletePost(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostResponseDTO> toggleLike(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(postService.toggleLike(id, currentUser.getId()));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<PostResponseDTO>> getRecommendations(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(postService.getRecommendations(currentUser.getId(), limit));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + email));
    }
}
