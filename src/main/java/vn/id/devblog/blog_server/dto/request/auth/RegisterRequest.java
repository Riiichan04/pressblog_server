package vn.id.devblog.blog_server.dto.request.auth;

public record RegisterRequest(
        String email,
        String username,
        String password
) {
}
