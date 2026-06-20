package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.models.StaticPage;

import java.util.Optional;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Long> {
    Optional<StaticPage> findBySlugAndIsPublishedTrueAndIsDeletedFalse(String slug);

    Optional<StaticPage> findBySlugAndIsDeletedFalse(String slug);

    Page<StaticPage> findAllByIsDeletedFalse(Pageable pageable);
}