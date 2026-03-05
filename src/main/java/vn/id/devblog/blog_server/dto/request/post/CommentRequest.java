package vn.id.devblog.blog_server.dto.request.post;

public record CommentRequest(
        Long postId,
        Long authorId,
        String content,
        Long commentId
) {
}
