package vn.id.devblog.blog_server.dto.response.post;

import java.time.LocalDateTime;

public record PublicPostResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String thumbnail,
        Long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
