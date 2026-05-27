package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Page<Comment> findByParentId(Long parentId, Pageable pageable);

    Page<Comment> findByPostIdAndParentIdIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
    Page<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);
    int countByParentId(Long parentId);
}
