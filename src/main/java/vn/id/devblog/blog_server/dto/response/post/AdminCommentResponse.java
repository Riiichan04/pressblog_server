package vn.id.devblog.blog_server.dto.response.post;

import vn.id.devblog.blog_server.common.enums.CommentStatus;

import java.time.LocalDateTime;

public record AdminCommentResponse(
        Long id,
        String content,
        String authorName,
        Long postId,
        String postName,
        boolean isDeleted,
        LocalDateTime createdAt,
        CommentStatus status
) {
}