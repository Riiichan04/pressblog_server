package vn.id.devblog.blog_server.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.devblog.blog_server.common.utilities.HtmlCleaner;
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    private static final int DEFAULT_POST_LENGTH = 100000;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository,
                       CategoryRepository categoryRepository, TagRepository tagRepository,
                       Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.cloudinary = cloudinary;
    }


    @Transactional
    public PostResponse insertNewPost(PostRequest request) {
        if (validatePostRequest(request)) {
            Post post = new Post();
            post.setName(request.name());
            post.setContent(HtmlCleaner.cleanHtml(request.content()));
            post.setSlug(SlugUtils.toSlug(request.name()));
            post.setThumbnail(request.thumbnail());
            post.setAuthor(userRepository.findByEmail(request.email()));
            post.setCategory(categoryRepository.findByName(request.categoryName()));
            post.setLanguage(request.language());
            Set<Tag> tags = this.extractTags(request.listTag());
            post.setTags(tags);
            postRepository.save(post);

            verifyImages(request.content(), request.thumbnail());

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
        post.setContent(HtmlCleaner.cleanHtml(request.content()));
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

        verifyImages(request.content(), request.thumbnail());

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

    public GetPostResponse getPostBySlug(String slug) {
        System.out.println(slug);
        Post post = postRepository.findBySlug(slug).orElse(null);
        if (post == null) return null;
        return mapPostToGetPostResponse(post);
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
        return posts.map(PostService::mapPostToGetPostResponse);
    }

    private static GetPostResponse mapPostToGetPostResponse(Post post) {
        return new GetPostResponse(
                post.getId(), post.getName(),
                post.getSlug(), post.getContent(),
                post.getThumbnail(), post.getAuthor().getId(),
                post.getCategory().getName(),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                post.getStatus(),
                post.getViewCount(),
                post.getUpdatedAt(), post.getLanguage()
        );
    }

    private String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary")) return null;
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            //Get folder and file name
            return lastPart.split("\\.")[0];
        } catch (Exception e) {
            return null;
        }
    }

    private void verifyImages(String content, String thumbnail) {
        Set<String> publicIds = new HashSet<>();

        // Find attachment by regex
        Pattern pattern = Pattern.compile("https://res.cloudinary.com/[^\\s\"')]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String id = extractPublicId(matcher.group());
            if (id != null) publicIds.add(id);
        }

        // Extract from thumbnail
        String thumbId = extractPublicId(thumbnail);
        if (thumbId != null) publicIds.add(thumbId);

        // Update tag in Cloudinary
        if (!publicIds.isEmpty()) {
            try {
                cloudinary.uploader().addTag("pressblog_verified",
                        publicIds.toArray(new String[0]), ObjectUtils.emptyMap());
                cloudinary.uploader().removeTag("pressblog_unverified",
                        publicIds.toArray(new String[0]), ObjectUtils.emptyMap());
            } catch (IOException e) {
                log.error("Failed to verify Cloudinary images: {}", e.getMessage());
            }
        }
    }
}
