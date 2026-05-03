package tn.esprit.tn.medicare_ai.service.interfaces;



import org.springframework.web.multipart.MultipartFile;
import tn.esprit.tn.medicare_ai.dto.request.SharedDocumentRequestDTO;
import tn.esprit.tn.medicare_ai.dto.response.SharedDocumentResponseDTO;
import java.util.List;

public interface SharedDocumentService {

    SharedDocumentResponseDTO uploadDocument(
            MultipartFile file,
            String description,
            Long sessionId
    );
    List<SharedDocumentResponseDTO> getDocumentsBySession(Long sessionId);
    SharedDocumentResponseDTO getDocumentById(Long id);
    void deleteDocument(Long id, Long uploaderId);
}
