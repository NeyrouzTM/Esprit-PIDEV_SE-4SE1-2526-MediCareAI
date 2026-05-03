package tn.esprit.tn.medicare_ai.repository;



import tn.esprit.tn.medicare_ai.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByPostId(Long postId);

    List<Reply> findByAuthorId(Long authorId);

    long countByPostId(Long postId);
}
