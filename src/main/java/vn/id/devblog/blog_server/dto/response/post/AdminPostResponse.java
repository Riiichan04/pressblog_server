package vn.id.devblog.blog_server.dto.response.post;

import vn.id.devblog.blog_server.common.enums.PostStatus;

import java.time.LocalDateTime;

public record AdminPostResponse(
        Long id,
        String title,
        String slug,
        String authorName,
        String categoryName,
        PostStatus status,
        boolean isDeleted,
        LocalDateTime createdAt
) {
}