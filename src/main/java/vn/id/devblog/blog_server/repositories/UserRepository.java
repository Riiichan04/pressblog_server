package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.dto.response.user.AuthorStatsDto;
import vn.id.devblog.blog_server.models.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchAuthors(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new vn.id.devblog.blog_server.dto.response.user.AuthorStatsDto(" +
            "u.username, u.avatar, COUNT(p.id), SUM(p.viewCount)) " +
            "FROM User u JOIN u.posts p " +
            "WHERE p.status = 'PUBLISHED' " +
            "GROUP BY u.id " +
            "ORDER BY (SUM(p.viewCount) * 0.7 + COUNT(p.id) * 100.0) DESC")
    List<AuthorStatsDto> findFeaturedAuthors(Pageable pageable);
}
