package vn.id.devblog.blog_server.dto.request.page;

public record StaticPageRequest(
        String title,
        String slug,
        String content,
        boolean isPublished
) {}

