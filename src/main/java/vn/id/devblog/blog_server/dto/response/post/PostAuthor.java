package vn.id.devblog.blog_server.dto.response.post;

public record PostAuthor(
        Long id,
        String email,
        String displayName,
        String username,
        String avatar,
        String description
) {
}
