package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByName(String name);
    Post findByNameAndAuthor(String name, User author);
    Post findBySlug(String slug);
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
}
