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
import vn.id.devblog.blog_server.dto.request.page.StaticPageRequest;
import vn.id.devblog.blog_server.dto.response.page.GetStaticPageResponse;
import vn.id.devblog.blog_server.dto.response.post.PostResponse;
import vn.id.devblog.blog_server.models.StaticPage;
import vn.id.devblog.blog_server.repositories.StaticPageRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class StaticPageService {

    private final StaticPageRepository staticPageRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public StaticPageService(StaticPageRepository staticPageRepository, Cloudinary cloudinary) {
        this.staticPageRepository = staticPageRepository;
        this.cloudinary = cloudinary;
    }

    @Transactional
    public PostResponse createPage(StaticPageRequest request) {
        StaticPage page = new StaticPage();
        page.setTitle(request.title());
        page.setContent(HtmlCleaner.cleanHtml(request.content()));

        String slug = (request.slug() != null && !request.slug().isBlank())
                ? SlugUtils.toSlug(request.slug())
                : SlugUtils.toSlug(request.title());
        page.setSlug(slug);

        page.setPublished(request.isPublished());

        staticPageRepository.save(page);
        verifyImages(request.content());

        return new PostResponse(true, "Create static page successfully");
    }

    @Transactional
    public PostResponse updatePage(Long id, StaticPageRequest request) {
        StaticPage page = staticPageRepository.findById(id).orElse(null);
        if (page == null || page.isDeleted()) {
            return new PostResponse(false, "Page not found");
        }

        page.setTitle(request.title());
        page.setContent(HtmlCleaner.cleanHtml(request.content()));

        if (request.slug() != null && !request.slug().isBlank()) {
            page.setSlug(SlugUtils.toSlug(request.slug()));
        }

        page.setPublished(request.isPublished());

        staticPageRepository.save(page);
        verifyImages(request.content());

        return new PostResponse(true, "Update static page successfully");
    }

    @Transactional
    public PostResponse deletePage(Long id) {
        StaticPage page = staticPageRepository.findById(id).orElse(null);
        if (page == null || page.isDeleted()) {
            return new PostResponse(false, "Page not found");
        }

        page.setDeleted(true);
        staticPageRepository.save(page);

        return new PostResponse(true, "Delete static page successfully");
    }

    public GetStaticPageResponse getPublicPageBySlug(String slug) {
        StaticPage page = staticPageRepository.findBySlugAndIsPublishedTrueAndIsDeletedFalse(slug).orElse(null);
        return mapToResponse(page);
    }

    public Page<GetStaticPageResponse> getAllPagesForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return staticPageRepository.findAllByIsDeletedFalse(pageable).map(this::mapToResponse);
    }

    private GetStaticPageResponse mapToResponse(StaticPage page) {
        if (page == null) return null;
        return new GetStaticPageResponse(
                page.getId(),
                page.getTitle(),
                page.getSlug(),
                page.getContent(),
                page.isPublished(),
                page.getUpdatedAt()
        );
    }

    private String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary")) return null;
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\.")[0];
        } catch (Exception e) {
            return null;
        }
    }

    private void verifyImages(String content) {
        Set<String> publicIds = new HashSet<>();
        Pattern pattern = Pattern.compile("https://res.cloudinary.com/[^\\s\"')]+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String id = extractPublicId(matcher.group());
            if (id != null) publicIds.add(id);
        }

        if (!publicIds.isEmpty()) {
            try {
                cloudinary.uploader().addTag("pressblog_verified",
                        publicIds.toArray(new String[0]), ObjectUtils.emptyMap());
                cloudinary.uploader().removeTag("pressblog_unverified",
                        publicIds.toArray(new String[0]), ObjectUtils.emptyMap());
            } catch (IOException e) {
                log.error("Failed to verify Cloudinary images in Static Page: {}", e.getMessage());
            }
        }
    }

    public GetStaticPageResponse getPageByIdForAdmin(Long id) {
        StaticPage page = staticPageRepository.findById(id).orElse(null);

        if (page == null || page.isDeleted()) {
            return null;
        }

        return mapToResponse(page);
    }
}