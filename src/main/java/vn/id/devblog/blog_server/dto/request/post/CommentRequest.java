package vn.id.devblog.blog_server.dto.request.post;

public record CommentRequest(
        Long postId,
        String content,
        Long commentId
) {
}
