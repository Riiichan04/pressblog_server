package vn.id.devblog.blog_server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.id.devblog.blog_server.models.Category;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.repositories.CategoryRepository;
import vn.id.devblog.blog_server.repositories.TagRepository;

import java.util.List;

@Service
public class PostMetadataService {
    CategoryRepository categoryRepository;
    TagRepository tagRepository;

    @Autowired
    public PostMetadataService(CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Category> getPaginationCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public List<Tag> findTagByNameLike(String name) {
        return tagRepository.getByNameLike(name);
    }

    public List<String> getTrendingTags() {
        return tagRepository.findTrendingTags(PageRequest.of(0, 10));
    }
}
