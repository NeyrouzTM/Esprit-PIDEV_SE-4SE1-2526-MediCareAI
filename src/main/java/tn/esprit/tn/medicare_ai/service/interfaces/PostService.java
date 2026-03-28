package tn.esprit.tn.medicare_ai.service.interfaces;


import tn.esprit.tn.medicare_ai.dto.request.PostRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.PostResponseDTO;
import java.util.List;

public interface PostService {

    PostResponseDTO createPost(PostRequestDTO dto, Long authorId);
    List<PostResponseDTO> getAllPosts();
    PostResponseDTO getPostById(Long id);
    PostResponseDTO updatePost(Long id, PostRequestDTO dto, Long currentUserId);
    void deletePost(Long id, Long currentUserId);
}
