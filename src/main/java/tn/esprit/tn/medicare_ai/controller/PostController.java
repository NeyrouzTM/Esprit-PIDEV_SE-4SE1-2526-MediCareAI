package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostRequestDTO dto,
            @RequestParam Long authorId) {

        PostResponseDTO created = postService.createPost(dto, authorId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
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
}
