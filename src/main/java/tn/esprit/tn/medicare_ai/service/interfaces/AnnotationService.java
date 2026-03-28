package tn.esprit.tn.medicare_ai.service.interfaces;


import tn.esprit.tn.medicare_ai.dto.request.AnnotationRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.AnnotationResponseDTO;
import java.util.List;

public interface AnnotationService {

    AnnotationResponseDTO createAnnotation(AnnotationRequestDTO dto, Long documentId, Long authorId);
    List<AnnotationResponseDTO> getAnnotationsByDocument(Long documentId);
    AnnotationResponseDTO getAnnotationById(Long id);
    AnnotationResponseDTO updateAnnotation(Long id, AnnotationRequestDTO dto, Long authorId);
    void deleteAnnotation(Long id, Long authorId);
}
