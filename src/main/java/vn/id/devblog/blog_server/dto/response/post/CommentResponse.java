package vn.id.devblog.blog_server.dto.response.post;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        String authorAvatar,
        String authorDisplayName,
        String content,
        int upvote,
        int downvote,
        Long parentId,
        int replyCount,
        LocalDateTime createdAt
) {
}
