package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
    Optional<Post> findByIsFeaturedTrue();
    Page<Post> findByAuthorUsernameAndStatus(String username, PostStatus status, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.status = :status AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPublishedPosts(@Param("keyword") String keyword, @Param("status") PostStatus status, Pageable pageable);

    List<Post> findTop5ByIsFeaturedFalseOrderByCreatedAtDesc();
    List<Post> findTop5ByViewCountOrderByIdDesc(long viewCount);

    long countByAuthorId(Long authorId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = :views WHERE p.slug = :slug")
    void updateViewCount(@Param("slug") String slug, @Param("views") long views);

    @Query("SELECT SUM(p.viewCount) FROM Post p WHERE p.author.id = :authorId")
    Long sumViewsByAuthorId(@Param("authorId") Long authorId);

    @Query("select viewCount from Post where slug = :slug")
    long findViewCountBySlug(String slug);

}
