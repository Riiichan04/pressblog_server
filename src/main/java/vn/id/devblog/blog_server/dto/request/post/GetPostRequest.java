package vn.id.devblog.blog_server.dto.request.post;

public record GetPostRequest(
        Long userId,
        int page,
        int size
) {
}
