package vn.id.devblog.blog_server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.dto.response.post.AdminPostResponse;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;

    public Page<AdminPostResponse> getAllPosts(int page, int size, String categorySlug) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;

        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            posts = postRepository.findByIsDeletedFalseAndCategory_Slug(categorySlug, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(post -> new AdminPostResponse(
                post.getId(),
                post.getName(),
                post.getSlug(),
                post.getAuthor().getUsername(),
                post.getCategory() != null ? post.getCategory().getName() : "No Category",
                post.getStatus(),
                post.isDeleted(),
                post.getCreatedAt()
        ));
    }

    public Page<AdminPostResponse> getPostsByStatus(PostStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByStatus(status, pageable);

        return posts.map(post -> new AdminPostResponse(
                post.getId(),
                post.getName(),
                post.getSlug(),
                post.getAuthor().getUsername(),
                post.getCategory() != null ? post.getCategory().getName() : "No Category",
                post.getStatus(),
                post.isDeleted(),
                post.getCreatedAt()
        ));
    }

    @Transactional
    public boolean forceDeletePost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return false;

        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean restorePost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return false;

        post.setDeleted(false);
        postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean updatePostStatus(Long postId, PostStatus newStatus) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return false;
        post.setStatus(newStatus);
        postRepository.save(post);
        return true;
    }
}