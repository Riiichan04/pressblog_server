package vn.id.devblog.blog_server.dto.response.page;

import java.time.LocalDateTime;

public record GetStaticPageResponse(
        Long id,
        String title,
        String slug,
        String content,
        boolean isPublished,
        LocalDateTime updatedAt
) {}