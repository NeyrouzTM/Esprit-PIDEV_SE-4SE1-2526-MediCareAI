package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

    List<Annotation> findByDocumentId(Long documentId);

    List<Annotation> findByAuthorId(Long authorId);
}