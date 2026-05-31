package vn.id.devblog.blog_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.models.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
    Category findBySlug(String slug);
    List<Category> findByIsDeletedFalse(boolean deleted);
}
