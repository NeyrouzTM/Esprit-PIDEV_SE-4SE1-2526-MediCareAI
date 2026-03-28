package tn.esprit.tn.medicare_ai.controller;



import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import tn.esprit.tn.medicare_ai.service.interfaces.ReplyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/posts/{postId}/replies")
    public ResponseEntity<ReplyResponseDTO> createReply(
            @PathVariable Long postId,
            @Valid @RequestBody ReplyRequestDTO dto,
            @RequestParam Long authorId) {

        ReplyResponseDTO created = replyService.createReply(dto, postId, authorId);
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
}