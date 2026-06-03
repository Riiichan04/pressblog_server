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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorIdAndIsDeletedFalse(Long authorId, Pageable pageable);
    Optional<Post> findFirstByIsFeaturedTrueAndIsDeletedFalseAndStatus(PostStatus status);
    Page<Post> findByAuthorUsernameAndStatusAndIsDeletedFalse(String username, PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.status = :status AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPublishedPosts(@Param("keyword") String keyword, @Param("status") PostStatus status, Pageable pageable);

    List<Post> findTop5ByIsDeletedFalseAndStatusOrderByCreatedAtDesc(PostStatus status);
    List<Post> findTop5ByIsDeletedFalseAndAuthorIdAndStatusOrderByViewCountDesc(Long userId, PostStatus status);

    long countByAuthorIdAndIsDeletedFalse(Long authorId);


    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = :views WHERE p.slug = :slug")
    void updateViewCount(@Param("slug") String slug, @Param("views") long views);

    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p WHERE p.author.id = :authorId AND p.isDeleted = false")
    Long sumViewsByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT p.viewCount FROM Post p WHERE p.slug = :slug AND p.isDeleted = false")
    long findViewCountBySlug(@Param("slug") String slug);

    int countByCategoryId(int categoryId);

    @Query("""
        SELECT p FROM Post p
        LEFT JOIN FETCH p.category c
        LEFT JOIN FETCH p.author a
        LEFT JOIN FETCH p.tags t
        WHERE p.slug = :slug
          AND p.isDeleted = false
          AND p.status = :status
    """)
    Optional<Post> findValidPublicPostBySlug(@Param("slug") String slug, @Param("status") PostStatus status);

    //For admin dashboard
    long countByIsDeletedFalse();
    long countByIsDeletedFalseAndStatus(PostStatus status);
    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p WHERE p.isDeleted = false")
    Long sumAllViews();

    List<Post> findByCreatedAtAfterAndIsDeletedFalse(LocalDateTime date);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByIsDeletedFalseAndCategory_Slug(String slug, Pageable pageable);
}
