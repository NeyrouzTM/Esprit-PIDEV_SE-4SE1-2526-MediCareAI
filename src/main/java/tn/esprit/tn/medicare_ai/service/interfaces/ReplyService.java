package tn.esprit.tn.medicare_ai.service.interfaces;



import tn.esprit.tn.medicare_ai.dto.request.ReplyRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.ReplyResponseDTO;
import java.util.List;

public interface ReplyService {

    ReplyResponseDTO createReply(ReplyRequestDTO dto, Long postId, Long authorId);
    List<ReplyResponseDTO> getRepliesByPost(Long postId);
    ReplyResponseDTO getReplyById(Long id);
    ReplyResponseDTO updateReply(Long id, ReplyRequestDTO dto, Long currentUserId);
    void deleteReply(Long id, Long currentUserId);
}
