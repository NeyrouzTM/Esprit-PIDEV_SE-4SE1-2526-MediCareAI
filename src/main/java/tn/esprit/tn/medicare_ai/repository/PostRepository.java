package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    // Pour chercher les posts premium
    List<Post> findByIsPremiumOnlyTrue();
}
