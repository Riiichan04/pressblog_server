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
import vn.id.devblog.blog_server.common.constants.PermissionConstants;
import vn.id.devblog.blog_server.common.constants.RoleConstants;
import vn.id.devblog.blog_server.common.enums.AppPermission;
import vn.id.devblog.blog_server.common.enums.PostStatus;
import vn.id.devblog.blog_server.common.utilities.HtmlCleaner;
import vn.id.devblog.blog_server.common.utilities.SlugUtils;
import vn.id.devblog.blog_server.dto.request.post.PostRequest;
import vn.id.devblog.blog_server.dto.response.post.GetPostResponse;
import vn.id.devblog.blog_server.dto.response.post.PostAuthor;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.Post;
import vn.id.devblog.blog_server.models.Tag;
import vn.id.devblog.blog_server.models.User;
import vn.id.devblog.blog_server.repositories.CategoryRepository;
import vn.id.devblog.blog_server.repositories.PostRepository;
import vn.id.devblog.blog_server.repositories.TagRepository;
import vn.id.devblog.blog_server.repositories.UserRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    private final PostViewService postViewService;

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository,
                       CategoryRepository categoryRepository, TagRepository tagRepository,
                       Cloudinary cloudinary, PostViewService postViewService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.cloudinary = cloudinary;
        this.postViewService = postViewService;
    }


    @Transactional
    public PostResponse insertNewPost(PostRequest request, User currentUser) {
        //Extract from validatePostRequest method
        if (request.content() == null || request.content().length() > DEFAULT_POST_LENGTH) {
            return new PostResponse(false, "Content not valid or too long");
        }

        Post post = new Post();
        post.setName(request.name());
        post.setContent(HtmlCleaner.cleanHtml(request.content()));
        post.setSlug(SlugUtils.toSlug(request.name()));
        post.setThumbnail(request.thumbnail());
        post.setAuthor(currentUser);
        post.setCategory(categoryRepository.findBySlug(request.categoryName()));
        post.setLanguage(request.language());
        post.setExcerpt(request.excerpt());
        post.setTags(this.extractTags(request.listTag()));
        post.setStatus(PostStatus.PENDING); //Confirm upload
        postRepository.save(post);
        verifyImages(request.content(), request.thumbnail());
        return new PostResponse(true, "Add new post successfully");
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, User currentUser) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return new PostResponse(false, "Post not found");
        }


        boolean isOwner = Objects.equals(post.getAuthor().getId(), currentUser.getId());

        boolean hasAdminRight = currentUser.getAuthorities().stream()
                .anyMatch(auth ->
                        Objects.equals(auth.getAuthority(), PermissionConstants.UPDATE_ANY_POST) ||
                                Objects.equals(auth.getAuthority(), RoleConstants.ROLE_ADMIN)
                );
        if (!isOwner && !hasAdminRight) {
            return new PostResponse(false, "You don't have permission to edit this blog");
        }

        post.setName(request.name());
        post.setContent(HtmlCleaner.cleanHtml(request.content()));
        post.setThumbnail(request.thumbnail());

        String newSlug = SlugUtils.toSlug(request.name());
        if (!post.getSlug().equals(newSlug)) {
            post.setSlug(SlugUtils.toSlug(newSlug));
        }

        post.setCategory(categoryRepository.findByName(request.categoryName()));
        post.getTags().clear();
        this.extractTags(request.listTag()).forEach(tag -> post.getTags().add(tag));

        postRepository.save(post);
        verifyImages(request.content(), request.thumbnail());
        return new PostResponse(true, "Update post successfully");
    }

    @Transactional
    public PostResponse deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return new PostResponse(false, "Post not found");
        }

        boolean isOwner = Objects.equals(post.getAuthor().getId(), currentUser.getId());
        boolean hasAdminRight = currentUser.getAuthorities().stream()
                .anyMatch(auth ->
                        Objects.equals(auth.getAuthority(), PermissionConstants.DELETE_ANY_POST) ||
                                Objects.equals(auth.getAuthority(), RoleConstants.ROLE_ADMIN));

        if (!isOwner && !hasAdminRight) {
            return new PostResponse(false, "You don't have permission to delete this blog");
        }

        post.setDeleted(true);
        postRepository.save(post);
        return new PostResponse(true, "Delete post successfully");
    }


    public Page<GetPostResponse> getPostByUser(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> listPost = postRepository.findByAuthorIdAndIsDeletedFalse(userId, pageable);
        return this.mapToResponse(listPost);
    }

    public GetPostResponse getPostBySlug(String slug) {
        Post post = postRepository.findValidPublicPostBySlug(slug, PostStatus.PUBLISHED).orElse(null);
        if (post == null) return null;
        GetPostResponse rawResponse = mapPostToGetPostResponse(post);
        //Map with redis view count
        return new GetPostResponse(
                rawResponse.id(),
                rawResponse.name(),
                rawResponse.slug(),
                rawResponse.content(),
                rawResponse.thumbnail(),
                rawResponse.author(),
                rawResponse.categoryName(),
                rawResponse.tagNames(),
                rawResponse.status(),
                postViewService.getViewCount(slug),
                rawResponse.updatedAt(),
                rawResponse.language(),
                rawResponse.excerpt()
        );
    }

    public GetPostResponse getFeaturedPost() {
        //TODO: Change to PostStatus.PUBLISHED after complete admin ui
        return mapPostToGetPostResponse(postRepository.findFirstByIsFeaturedTrueAndIsDeletedFalseAndStatus(PostStatus.PUBLISHED).orElse(null));
    }

    public List<GetPostResponse> getNewestPost() {
        return postRepository.findTop5ByIsDeletedFalseAndStatusOrderByCreatedAtDesc(PostStatus.PUBLISHED).stream().map(PostService::mapPostToGetPostResponse).collect(Collectors.toList());
    }

    private Set<Tag> extractTags(Set<String> rawTags) {
        if (rawTags == null) return new HashSet<>();
        return rawTags.stream()
                .map(tagName -> {
                    Tag existingTag = tagRepository.findByName(tagName);
                    if (existingTag != null) return existingTag;

                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return tagRepository.save(newTag);
                })
                .collect(Collectors.toSet());
    }

    private Page<GetPostResponse> mapToResponse(Page<Post> posts) {
        return posts.map(PostService::mapPostToGetPostResponse);
    }

    private static GetPostResponse mapPostToGetPostResponse(Post post) {
        if (post == null) return null;
        PostAuthor author = new PostAuthor(
                post.getAuthor().getId(),
                post.getAuthor().getEmail(),
                post.getAuthor().getDisplayName(),
                post.getAuthor().getUsername(),
                post.getAuthor().getAvatar(),
                post.getAuthor().getDescription()
        );

        return new GetPostResponse(
                post.getId(), post.getName(),
                post.getSlug(), post.getContent(),
                post.getThumbnail(), author,
                (post.getCategory() != null && !post.getCategory().isDeleted()) ? post.getCategory().getName() : "",
                post.getTags() != null ? post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()) : new HashSet<>(),
                post.getStatus(),
                post.getViewCount(),
                post.getUpdatedAt(), post.getLanguage(), post.getExcerpt()
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
