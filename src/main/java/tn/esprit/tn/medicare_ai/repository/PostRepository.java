package tn.esprit.tn.medicare_ai.repository;

import tn.esprit.tn.medicare_ai.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByIsPremiumOnlyTrue();

    List<Post> findByIsPremiumOnlyFalse();
    @Query("SELECT DISTINCT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllWithLikes();

    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithLikes(@Param("id") Long id);

    // Posts contenant au moins un tag de la liste (pour recommandations par tags)
    @Query("SELECT DISTINCT p FROM Post p WHERE p.id != :excludeId AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags)")
    List<Post> findByTagsInAndIdNot(@Param("tags") Set<String> tags, @Param("excludeId") Long excludeId);

    // Posts non encore vus par l'utilisateur (pas dans ses likes), triés par popularité
    @Query("SELECT DISTINCT p FROM Post p WHERE p.id NOT IN :viewedIds ORDER BY SIZE(p.likedByUserIds) DESC")
    List<Post> findNotViewedOrderByLikes(@Param("viewedIds") List<Long> viewedIds);

    // Tous les posts triés par likes DESC puis date DESC
    @Query("SELECT DISTINCT p FROM Post p ORDER BY SIZE(p.likedByUserIds) DESC, p.createdAt DESC")
    List<Post> findAllOrderByPopularity();
}
