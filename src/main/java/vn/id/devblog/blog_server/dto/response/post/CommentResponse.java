package vn.id.devblog.blog_server.dto.response.post;

public record CommentResponse(
        Long id,
        Long postId,
        Long authorId,
        String content,
        int upvote,
        int downvote,
        Long parentId
) {
}
