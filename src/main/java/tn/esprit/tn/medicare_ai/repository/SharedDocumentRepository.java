package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.SharedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SharedDocumentRepository extends JpaRepository<SharedDocument, Long> {

    List<SharedDocument> findBySessionId(Long sessionId);

    List<SharedDocument> findByUploaderId(Long uploaderId);
}