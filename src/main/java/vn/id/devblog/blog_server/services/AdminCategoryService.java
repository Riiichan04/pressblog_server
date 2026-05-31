package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.constants.RoleConstants;
import vn.id.devblog.blog_server.common.utilities.SlugUtils;
import vn.id.devblog.blog_server.dto.request.post.CategoryRequest;
import vn.id.devblog.blog_server.models.Category;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.CategoryRepository;
import vn.id.devblog.blog_server.repositories.PostRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    @Transactional
    public Category createCategory(CategoryRequest request) {
        String slug = SlugUtils.toSlug(request.name());
        if (categoryRepository.findBySlug(slug) != null) {
            throw new IllegalArgumentException("Category already exists!");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setSlug(slug);
        category.setDescription(request.description());

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            log.info("Category id {} not found", id);
            return null;
        }

        category.setName(request.name());
        category.setSlug(SlugUtils.toSlug(request.name()));
        category.setDescription(request.description());

        return categoryRepository.save(category);
    }

    @Transactional
    public boolean deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null || category.isDeleted()) {
            return false;
        }

        long postCount = postRepository.countByCategoryId(id);
        if (postCount > 0) {
            log.info("Can't delete. Currently have {} blog belong to this category.", postCount);
            return false;
        }

        category.setDeleted(true);
        categoryRepository.save(category);
        return true;
    }

    @Transactional
    public boolean restoreCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null || !category.isDeleted()) return false;

        category.setDeleted(false);
        categoryRepository.save(category);
        return true;
    }

    @Transactional
    public boolean forceDeleteCategory(Integer id, User user) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null || category.isDeleted() ||
                user.getRole() == null || !Objects.equals(user.getRole().getName(), RoleConstants.ROLE_ADMIN)) {
            return false;
        }
        category.setDeleted(true);
        categoryRepository.save(category);
        log.info("Force deleted category id {}", id);
        return true;
    }
}