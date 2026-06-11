package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.models.Tag;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
    Tag findBySlug(String slug);
    Page<Tag> findAll(Pageable pageable);
    List<Tag> getByNameLike(String name);

    @Query("SELECT t.name FROM Tag t JOIN t.posts p " +
            "WHERE p.status = 'PUBLISHED' " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(p.id) DESC")
    List<String> findTrendingTags(Pageable pageable);
}
