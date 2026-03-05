package vn.id.devblog.blog_server.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.utilities.SlugUtils;
import vn.id.devblog.blog_server.dto.request.post.PostRequest;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.repositories.CategoryRepository;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.TagRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    private static final int DEFAULT_POST_LENGTH = 100000;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public PostResponse insertNewPost(PostRequest request) {
        if (validatePostRequest(request)) {
            Post post = new Post();
            post.setName(request.name());
            post.setContent(request.content());
            post.setSlug(SlugUtils.toSlug(request.name()));
            post.setThumbnail(request.thumbnail());
            post.setAuthor(userRepository.findByEmail(request.email()));
            post.setCategory(categoryRepository.findByName(request.categoryName()));
            Set<Tag> tags = this.extractTags(request.listTag());
            post.setTags(tags);
            postRepository.save(post);
            return new PostResponse(true, "Add new post successfully");
        } else return new PostResponse(false, "Failed to add new post");
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return new PostResponse(false, "Post not found");
        }

        post.setName(request.name());
        post.setContent(request.content());
        post.setThumbnail(request.thumbnail());

        //Update slug
        String newSlug = SlugUtils.toSlug(request.name());
        if (!post.getSlug().equals(newSlug)) {
            post.setSlug(SlugUtils.toSlug(newSlug));
        }

        // Update categories and tags
        post.setCategory(categoryRepository.findByName(request.categoryName()));
        post.getTags().clear();
        this.extractTags(request.listTag()).forEach(tag -> post.getTags().add(tag));

        postRepository.save(post);
        return new PostResponse(true, "Update post successfully");
    }

    @Transactional
    public PostResponse deletePost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return new PostResponse(false, "Post not found");
        }
        post.setDeleted(true);
        postRepository.save(post);
        return new PostResponse(true, "Delete post successfully");
    }

    public Page<GetPostResponse> getPostByUser(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> listPost = postRepository.findByAuthorId(userId, pageable);
        return this.mapToResponse(listPost);
    }

    private boolean validatePostRequest(PostRequest request) {
        return userRepository.findByEmail(request.email()) != null && request.content().length() <= DEFAULT_POST_LENGTH;
    }

    private Set<Tag> extractTags(Set<String> rawTags) {
        return rawTags.stream()
                .map(tagRepository::findByName)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Page<GetPostResponse> mapToResponse(Page<Post> posts) {
        return posts.map(post ->
                new GetPostResponse(
                        post.getId(), post.getName(),
                        post.getSlug(), post.getContent(),
                        post.getThumbnail(), post.getAuthor().getId(),
                        post.getCategory().getName(),
                        post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                        post.getStatus(),
                        post.getViewCount()
                )
        );
    }
}
