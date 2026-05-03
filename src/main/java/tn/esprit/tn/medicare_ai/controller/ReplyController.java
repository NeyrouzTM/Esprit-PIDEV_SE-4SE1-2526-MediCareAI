package tn.esprit.tn.medicare_ai.controller;

import jakarta.persistence.EntityNotFoundException;
import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ReplyController {

    private final ReplyService replyService;
    private final UserRepository userRepository;

    public ReplyController(ReplyService replyService, UserRepository userRepository) {
        this.replyService = replyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts/{postId}/replies")
    public ResponseEntity<ReplyResponseDTO> createReply(
            @PathVariable Long postId,
            @Valid @RequestBody ReplyRequestDTO dto) {
        ReplyResponseDTO created = replyService.createReply(dto, postId, dto.getAuthorId());
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<List<ReplyResponseDTO>> getRepliesByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(replyService.getRepliesByPost(postId));
    }

    @GetMapping("/replies/{id}")
    public ResponseEntity<ReplyResponseDTO> getReplyById(@PathVariable Long id) {
        return ResponseEntity.ok(replyService.getReplyById(id));
    }

    @PutMapping("/replies/{id}")
    public ResponseEntity<ReplyResponseDTO> updateReply(
            @PathVariable Long id,
            @Valid @RequestBody ReplyRequestDTO dto,
            @RequestParam Long currentUserId) {
        return ResponseEntity.ok(replyService.updateReply(id, dto, currentUserId));
    }

    @DeleteMapping("/replies/{id}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long id,
            @RequestParam Long currentUserId) {
        replyService.deleteReply(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/replies/{id}/like")
    public ResponseEntity<ReplyResponseDTO> toggleLike(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(replyService.toggleLike(id, currentUser.getId()));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouve : " + email));
    }
}
